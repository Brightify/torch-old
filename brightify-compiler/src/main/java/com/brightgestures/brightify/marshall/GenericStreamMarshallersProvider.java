package com.brightgestures.brightify.marshall;

import com.brightgestures.brightify.util.TypeHelper;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class GenericStreamMarshallersProvider implements StreamMarshallerProvider {

    private final Map<Class<?>, StreamMarshallerInfo> genericStreamMarshallers = new HashMap<>();

    public GenericStreamMarshallersProvider() {
        addSingletonMarshaller(Boolean.class, "com.brightgestures.brightify.marshall.stream.BooleanMarshaller");
        addSingletonMarshaller(Byte.class, "com.brightgestures.brightify.marshall.stream.ByteMarshaller");
        addSingletonMarshaller(Short.class, "com.brightgestures.brightify.marshall.stream.ShortMarshaller");
        addSingletonMarshaller(Integer.class, "com.brightgestures.brightify.marshall.stream.IntegerMarshaller");
        addSingletonMarshaller(Long.class, "com.brightgestures.brightify.marshall.stream.LongMarshaller");

        addSingletonMarshaller(Float.class, "com.brightgestures.brightify.marshall.stream.FloatMarshaller");
        addSingletonMarshaller(Double.class, "com.brightgestures.brightify.marshall.stream.DoubleMarshaller");

        addSingletonMarshaller(String.class, "com.brightgestures.brightify.marshall.stream.StringMarshaller");
    }

    private void addSingletonMarshaller(Class<?> cls, String marshallerTypeFullName) {
        genericStreamMarshallers.put(cls, new SingletonStreamMarshallerInfo(marshallerTypeFullName));
    }

    @Override
    public boolean isSupported(TypeHelper typeHelper, TypeMirror typeMirror) {
        return getMarshallerInfo(typeHelper, typeMirror) != null;
    }

    @Override
    public StreamMarshallerInfo getMarshallerInfo(TypeHelper typeHelper, TypeMirror typeMirror) {
        TypeMirror propertyType = typeHelper.getWrappedType(typeMirror);
        Types typeUtils = typeHelper.getProcessingEnvironment().getTypeUtils();
        for(Class<?> cls : genericStreamMarshallers.keySet()) {
            if(typeUtils.isSameType(typeHelper.typeOf(cls), propertyType)) {
                return genericStreamMarshallers.get(cls);
            }
        }
        return null;
    }
}
