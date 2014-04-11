package org.brightify.torch.marshall;

import com.google.inject.Inject;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
            if (Modifier.isAbstract(marshallerClass.getModifiers())) {
                continue;
            }
            Map<TypeVariable, Type> map = params(Marshaller2.class, marshallerClass, null);

            Class<?> cls = (Class<?>) map.values().iterator().next();
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

    Map<TypeVariable, Type> params(Class<?> targetClass, Class<?> cls, ParameterizedType type) {
        TypeVariable[] parameters = cls.getTypeParameters();
        Type[] actualParameters;
        if (type != null) {
            actualParameters = type.getActualTypeArguments();
        } else {
            actualParameters = parameters;
        }

        Map<TypeVariable, Type> map = new HashMap<TypeVariable, Type>();
        for (int i = 0; i < parameters.length; i++) {
            map.put(parameters[i], actualParameters[i]);
        }
        if (cls == targetClass) {
            return map;
        }

        List<Type> genericParents = new ArrayList<Type>();

        Type[] genericInterfaces = cls.getGenericInterfaces();
        Type genericSuperclass = cls.getGenericSuperclass();

        Collections.addAll(genericParents, genericInterfaces);
        genericParents.add(genericSuperclass);

        for (Type genericInterface : genericParents) {
            ParameterizedType parameterizedInterface = (ParameterizedType) genericInterface;

            Map<TypeVariable, Type> genericInterfaceMap = params(targetClass,
                                                                 (Class) parameterizedInterface.getRawType(),
                                                                 parameterizedInterface);
            if (genericInterfaceMap != null) {
                for (TypeVariable key : genericInterfaceMap.keySet()) {
                    Type value = genericInterfaceMap.get(key);
                    if (value instanceof TypeVariable) {
                        genericInterfaceMap.put(key, map.get(value));
                    }
                }
                return genericInterfaceMap;
            }
        }
        return null;
    }

    private Class<?> getGenericParameter(Class<?> cls, Type type) {
        if (cls == null) {
            return null;
        }
        if (cls != Marshaller2.class || type == null) {
            Class<?> superClass = cls.getSuperclass();
            Class<?> parameter = getGenericParameter(superClass, null);
            if (parameter != null) {
                return parameter;
            }

            Class<?>[] interfaces = cls.getInterfaces();
            Type[] genericInterfaces = cls.getGenericInterfaces();
            for (int i = 0; i < genericInterfaces.length; i++) {
                parameter = getGenericParameter(interfaces[i], genericInterfaces[i]);
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
