package com.brightgestures.brightify.marshall;

import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Ref;
import com.brightgestures.brightify.generate.Field;
import com.brightgestures.brightify.generate.GenericField;
import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.util.TypeHelper;

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
        internalCursorMarshallers.put(Key.class, "com.brightgestures.brightify.marshall.cursor.KeyCursorMarshaller");
        internalCursorMarshallers.put(Ref.class, "com.brightgestures.brightify.marshall.cursor.RefCursorMarshaller");
    }

    @Override
    public boolean isSupported(TypeHelper typeHelper, Property property) {
        return getMarshallerInfo(typeHelper, property) != null;
    }

    @Override
    public CursorMarshallerInfo getMarshallerInfo(TypeHelper typeHelper, Property property) {
        if (createdMarshallerInfos.containsKey(property.type)) {
            return createdMarshallerInfos.get(property.type);
        }

        Class<?> type = null;
        Types typeUtils = typeHelper.getProcessingEnvironment().getTypeUtils();
        TypeMirror propertyTypeErasure = typeUtils.erasure(property.type);
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

        DeclaredType declaredType = (DeclaredType) property.type;
        TypeMirror entityType = declaredType.getTypeArguments().iterator().next();

        Field entityField = new Field();
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

        createdMarshallerInfos.put(property.type, marshallerInfo);

        return marshallerInfo;
    }
}
