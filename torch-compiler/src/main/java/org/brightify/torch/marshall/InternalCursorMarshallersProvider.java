package org.brightify.torch.marshall;

import org.brightify.torch.Key;
import org.brightify.torch.Ref;
import org.brightify.torch.generate.Field;
import org.brightify.torch.generate.FieldImpl;
import org.brightify.torch.generate.GenericField;
import org.brightify.torch.parse.Property;
import org.brightify.torch.util.TypeHelper;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class InternalCursorMarshallersProvider implements CursorMarshallerProvider {

    private final Map<Class<?>, String> internalCursorMarshallers = new HashMap<Class<?>, String>();
    private final Map<TypeMirror, CursorMarshallerInfo> createdMarshallerInfos = new HashMap<TypeMirror,
            CursorMarshallerInfo>();

    public InternalCursorMarshallersProvider() {
        internalCursorMarshallers.put(Key.class, "org.brightify.torch.marshall.cursor.KeyCursorMarshaller");
        internalCursorMarshallers.put(Ref.class, "org.brightify.torch.marshall.cursor.RefCursorMarshaller");
    }

    @Override
    public boolean isSupported(TypeHelper typeHelper, Property property) {
        return getMarshallerInfo(typeHelper, property) != null;
    }

    @Override
    public CursorMarshallerInfo getMarshallerInfo(TypeHelper typeHelper, Property property) {
        if (createdMarshallerInfos.containsKey(property.getType())) {
            return createdMarshallerInfos.get(property.getType());
        }

        Class<?> type = null;
        Types typeUtils = typeHelper.getProcessingEnvironment().getTypeUtils();
        TypeMirror propertyTypeErasure = typeUtils.erasure(property.getType());
        for (Class<?> supportedType : internalCursorMarshallers.keySet()) {
            if (typeUtils.isSameType(typeUtils.getDeclaredType(typeHelper.elementOf(supportedType)),
                    propertyTypeErasure)) {
                type = supportedType;
                break;
            }
        }
        if (type == null) {
            return null;
        }

        DeclaredType declaredType = (DeclaredType) property.getType();
        TypeMirror entityType = declaredType.getTypeArguments().iterator().next();

        FieldImpl entityField = new FieldImpl();
        entityField.setTypeFullName(entityType.toString());

        final GenericField field = new GenericField(entityField);
        field.setTypeFullName(internalCursorMarshallers.get(type));
        field.setValue(field.getErasuredType() + ".getInstance(" + entityField.getTypeSimpleName() + ".class)");

        CursorMarshallerInfo marshallerInfo = new CursorMarshallerInfo() {
            @Override
            public Field getField() {
                return field;
            }
        };

        createdMarshallerInfos.put(property.getType(), marshallerInfo);

        return marshallerInfo;
    }
}
