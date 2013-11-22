package com.brightgestures.brightify.marshall;

import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.util.TypeHelper;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class GenericCursorMarshallersProvider implements CursorMarshallerProvider {

    private final Map<Class<?>, CursorMarshallerInfo> genericCursorMarshallers = new HashMap<>();

    public GenericCursorMarshallersProvider() {
        addSingletonMarshaller(Boolean.class, "com.brightgestures.brightify.marshall.cursor.BooleanMarshaller");
        addSingletonMarshaller(Byte.class, "com.brightgestures.brightify.marshall.cursor.ByteMarshaller");
        addSingletonMarshaller(Short.class, "com.brightgestures.brightify.marshall.cursor.ShortMarshaller");
        addSingletonMarshaller(Integer.class, "com.brightgestures.brightify.marshall.cursor.IntegerMarshaller");
        addSingletonMarshaller(Long.class, "com.brightgestures.brightify.marshall.cursor.LongMarshaller");

        addSingletonMarshaller(Float.class, "com.brightgestures.brightify.marshall.cursor.FloatMarshaller");
        addSingletonMarshaller(Double.class, "com.brightgestures.brightify.marshall.cursor.DoubleMarshaller");

        addSingletonMarshaller(String.class, "com.brightgestures.brightify.marshall.cursor.StringMarshaller");
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
        TypeMirror propertyType = typeHelper.getWrappedType(property.type);
        Types typeUtils = typeHelper.getProcessingEnvironment().getTypeUtils();
        for(Class<?> cls : genericCursorMarshallers.keySet()) {
            if(typeUtils.isSameType(typeHelper.typeOf(cls), propertyType)) {
                return genericCursorMarshallers.get(cls);
            }
        }
        return null;
    }
}
