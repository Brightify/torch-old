package org.brightify.torch.compile.util;

import com.google.common.primitives.Primitives;
import com.google.inject.Inject;
import org.brightify.torch.compile.Property;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class TypeHelperImpl implements TypeHelper {


    @Inject
    private ProcessingEnvironment environment;

    @Inject
    private Types types;

    @Inject
    private Elements elements;


    @Override
    public ProcessingEnvironment getProcessingEnvironment() {
        return environment;
    }

    @Override
    public TypeMirror getWrappedType(Property property) {
        return getWrappedType(property.getType());
    }

    @Override
    public TypeMirror getWrappedType(TypeMirror propertyType) {
        if (propertyType.getKind().isPrimitive()) {
            propertyType = environment.getTypeUtils().boxedClass(
                    (PrimitiveType) propertyType).asType();
        }
        return propertyType;
    }

    @Override
    public TypeElement elementOf(Class<?> cls) {
        return elements.getTypeElement(cls.getName());
    }

    @Override
    public TypeMirror typeOf(Class<?> cls) {
        boolean wasArray = false;
        boolean wasPrimitive = false;
        if (cls.isArray()) {
            cls = cls.getComponentType();
            wasArray = true;
        }
        if (cls.isPrimitive()) {
            cls = Primitives.wrap(cls);
            wasPrimitive = true;
        }

        TypeMirror typeMirror = elementOf(cls).asType();

        if (wasPrimitive) {
            typeMirror = types.unboxedType(typeMirror);
        }

        if (wasArray) {
            typeMirror = types.getArrayType(typeMirror);
        }

        return typeMirror;
    }

    @Override
    public Class<?> classOf(TypeMirror mirror) {
        try {
            return Class.forName(mirror.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String packageOf(Element element) {
        String[] entityPackages = element.toString().split("\\.");
        if (entityPackages.length == 1) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < entityPackages.length - 1; i++) {
                if (i > 0) {
                    builder.append(".");
                }
                builder.append(entityPackages[i]);
            }
            return builder.toString();
        }
    }

    @Override
    public Map<TypeVariable<?>, Type> genericParameters(Class<?> targetClass, Class<?> cls, ParameterizedType type) {
        TypeVariable<?>[] parameters = cls.getTypeParameters();
        Type[] actualParameters;
        if (type != null) {
            actualParameters = type.getActualTypeArguments();
        } else {
            actualParameters = parameters;
        }

        Map<TypeVariable<?>, Type> map = new HashMap<TypeVariable<?>, Type>();
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
            Class<?> rawClass = (Class<?>) parameterizedInterface.getRawType();

            Map<TypeVariable<?>, Type> genericInterfaceMap = genericParameters(targetClass, rawClass,
                                                                               parameterizedInterface);
            if (genericInterfaceMap != null) {
                for (TypeVariable<?> key : genericInterfaceMap.keySet()) {
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

    @Override
    public List<? extends TypeMirror> genericParameters(TypeMirror type) {
        if(type instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) type;
            return declaredType.getTypeArguments();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public TypeMirror singleGenericParameter(TypeMirror type) {
        List<? extends TypeMirror> parameters = genericParameters(type);

        if(parameters.size() != 1) {
            throw new IllegalStateException("Number of generic parameters was not 1!");
        }

        return parameters.iterator().next();
    }

    private DeclaredType listOf(Class<?> bound) {
        return listOf(bound != null ? typeOf(bound) : null);
    }

    private DeclaredType listOf(TypeMirror bound) {
        return environment.getTypeUtils().getDeclaredType(environment.getElementUtils().getTypeElement(
                "java.util.List"), bound);
    }


}
