package org.brightify.torch.marshall;

import org.brightify.torch.parse.Property;
import org.brightify.torch.util.TypeHelper;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class GenericCursorMarshallersProvider implements CursorMarshallerProvider {

    private final Map<Class<?>, CursorMarshallerInfo> genericCursorMarshallers = new HashMap<Class<?>,
            CursorMarshallerInfo>();

    public GenericCursorMarshallersProvider() {
        addSingletonMarshaller(Boolean.class, "org.brightify.torch.marshall.cursor.BooleanCursorMarshaller");
        addSingletonMarshaller(Byte.class, "org.brightify.torch.marshall.cursor.ByteCursorMarshaller");
        addSingletonMarshaller(Short.class, "org.brightify.torch.marshall.cursor.ShortCursorMarshaller");
        addSingletonMarshaller(Integer.class, "org.brightify.torch.marshall.cursor.IntegerCursorMarshaller");
        addSingletonMarshaller(Long.class, "org.brightify.torch.marshall.cursor.LongCursorMarshaller");

        addSingletonMarshaller(Float.class, "org.brightify.torch.marshall.cursor.FloatCursorMarshaller");
        addSingletonMarshaller(Double.class, "org.brightify.torch.marshall.cursor.DoubleCursorMarshaller");

        addSingletonMarshaller(String.class, "org.brightify.torch.marshall.cursor.StringCursorMarshaller");
    }

    private void addSingletonMarshaller(Class<?> cls, String marshallerTypeFullName) {
        genericCursorMarshallers.put(cls, new SingletonCursorMarshallerInfo(marshallerTypeFullName));
    }

    @Override
    public boolean isSupported(TypeHelper typeHelper, Property property) {
        return getMarshallerInfo(typeHelper, property) != null;
    }

    @Override
    public CursorMarshallerInfo getMarshallerInfo(TypeHelper typeHelper, Property property) {
        TypeMirror propertyType = typeHelper.getWrappedType(property.getType());
        Types typeUtils = typeHelper.getProcessingEnvironment().getTypeUtils();
        for (Class<?> cls : genericCursorMarshallers.keySet()) {
            if (typeUtils.isSameType(typeHelper.typeOf(cls), propertyType)) {
                return genericCursorMarshallers.get(cls);
            }
        }
        return null;
    }
}
