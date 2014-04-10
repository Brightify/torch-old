package org.brightify.torch.marshall;

import com.google.inject.Inject;
import org.reflections.Reflections;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class MarshallerProvider2Impl implements MarshallerProvider2 {

    private final Map<Class<?>, Marshaller2<?>> marshallers = new HashMap<Class<?>, Marshaller2<?>>();

    @Inject
    public MarshallerProvider2Impl(Reflections reflections) {
        Set<Class<? extends Marshaller2>> marshallerClasses = reflections.getSubTypesOf(Marshaller2.class);

        for (Class<? extends Marshaller2> marshallerClass : marshallerClasses) {
            Class<?> cls = getGenericParameter(marshallerClass, null);
            if (cls == null) {
                // FIXME log warning that we couldn't find generic parameter
                continue;
            }
            try {
                marshallers.put(cls, marshallerClass.newInstance());
            } catch (Exception e) {
                // FIXME we shouldn't throw an exception because of Guice!
                throw new RuntimeException(
                        "Couldn't instantiate cursor marshaller provider " + marshallerClass.getSimpleName() + "!", e);
            }
        }
    }

    private Class<?> getGenericParameter(Class<?> cls, Type type) {
        if (cls == null) {
            return null;
        }
        if (cls != Marshaller2.class || type == null) {
            Class[] interfaces = cls.getInterfaces();
            Type[] genericInterfaces = cls.getGenericInterfaces();
            for (int i = 0; i < genericInterfaces.length; i++) {
                Class<?> parameter = getGenericParameter(interfaces[i], genericInterfaces[i]);
                if (parameter != null) {
                    return parameter;
                }
            }
            return null;
        }

        return (Class<?>) (ParameterizedType.class.cast(type)).getActualTypeArguments()[0];
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> Marshaller2<T> getMarshaller(Class<T> marshalledClass) {
        return (Marshaller2<T>) marshallers.get(marshalledClass);
    }
}
