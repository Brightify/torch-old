package org.brightify.torch.marshall;

import org.brightify.torch.util.TypeHelper;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class GenericStreamMarshallersProvider implements StreamMarshallerProvider {

    private final Map<Class<?>, StreamMarshallerInfo> genericStreamMarshallers = new HashMap<Class<?>,
            StreamMarshallerInfo>();

    public GenericStreamMarshallersProvider() {
        addSingletonMarshaller(Boolean.class, "org.brightify.torch.marshall.stream.BooleanStreamMarshaller");
        addSingletonMarshaller(Byte.class, "org.brightify.torch.marshall.stream.ByteStreamMarshaller");
        addSingletonMarshaller(Short.class, "org.brightify.torch.marshall.stream.ShortStreamMarshaller");
        addSingletonMarshaller(Integer.class, "org.brightify.torch.marshall.stream.IntegerStreamMarshaller");
        addSingletonMarshaller(Long.class, "org.brightify.torch.marshall.stream.LongStreamMarshaller");

        addSingletonMarshaller(Float.class, "org.brightify.torch.marshall.stream.FloatStreamMarshaller");
        addSingletonMarshaller(Double.class, "org.brightify.torch.marshall.stream.DoubleStreamMarshaller");

        addSingletonMarshaller(String.class, "org.brightify.torch.marshall.stream.StringStreamMarshaller");
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
        for (Class<?> cls : genericStreamMarshallers.keySet()) {
            if (typeUtils.isSameType(typeHelper.typeOf(cls), propertyType)) {
                return genericStreamMarshallers.get(cls);
            }
        }
        return null;
    }
}
