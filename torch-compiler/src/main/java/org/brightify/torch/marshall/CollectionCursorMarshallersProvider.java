package org.brightify.torch.marshall;

import org.brightify.torch.generate.Field;
import org.brightify.torch.generate.FieldImpl;
import org.brightify.torch.generate.GenericField;
import org.brightify.torch.parse.Property;
import org.brightify.torch.util.TypeHelper;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class CollectionCursorMarshallersProvider implements CursorMarshallerProvider {

    private final Map<Class<?>, String> collectionCursorMarshallers = new HashMap<Class<?>, String>();
    private final Map<TypeMirror, CursorMarshallerInfo> createdMarshallerInfos = new HashMap<TypeMirror,
            CursorMarshallerInfo>();

    public CollectionCursorMarshallersProvider() {
        collectionCursorMarshallers.put(List.class, "org.brightify.torch.marshall.cursor" +
                ".ArrayListCursorMarshaller");
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

        Class<?> collection = null;
        Types typeUtils = typeHelper.getProcessingEnvironment().getTypeUtils();
        TypeMirror propertyTypeErasure = typeUtils.erasure(property.getType());
        for (Class<?> supportedCollection : collectionCursorMarshallers.keySet()) {
            if (typeUtils.isSameType(typeUtils.getDeclaredType(typeHelper.elementOf(supportedCollection)),
                    propertyTypeErasure)) {
                collection = supportedCollection;
                break;
            }
        }
        if (collection == null) {
            return null;
        }

        DeclaredType declaredType = (DeclaredType) property.getType();
        TypeMirror itemType = declaredType.getTypeArguments().iterator().next();
        StreamMarshallerInfo itemMarshallerInfo = typeHelper.getStreamMarshallerInfo(itemType);
        if (itemMarshallerInfo == null) {
            return null;
        }

        Field itemMarshallerField = itemMarshallerInfo.getField();

        FieldImpl itemField = new FieldImpl();
        // FIXME find out why is "java.lang.String" passed in here!
        itemField.setTypeFullName("java.lang.String");

        final GenericField field = new GenericField(itemField);
        field.setTypeFullName(collectionCursorMarshallers.get(collection));
        field.setValue(field.getErasuredType() + ".getInstance(" + itemMarshallerField.getValue() + ")");
        field.getImports().addAll(itemMarshallerField.getImports());

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
