package org.brightify.torch.compile.util;

import com.google.common.primitives.Primitives;
import com.google.inject.Inject;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import org.brightify.torch.compile.PropertyMirror;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
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
    public TypeMirror getWrappedType(PropertyMirror propertyMirror) {
        return getWrappedType(propertyMirror.getType());
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
        if (type instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) type;
            return declaredType.getTypeArguments();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public TypeMirror singleGenericParameter(TypeMirror type) {
        List<? extends TypeMirror> parameters = genericParameters(type);

        if (parameters.size() != 1) {
            throw new IllegalStateException("Number of generic parameters was not 1!");
        }

        return parameters.iterator().next();
    }

    public <A extends Annotation> A getAnnotation(final List<? extends AnnotationMirror> annotationMirrors,
                                                  final Class<A> annotationClass) {
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            if(annotationMirror.getAnnotationType().toString().equals(annotationClass.getCanonicalName())) {
                return getAnnotation(annotationMirror, annotationClass);
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private <A extends Annotation> A getAnnotation(final AnnotationMirror annotationMirror,
                                                   final Class<A> annotationClass) {
        final Map<String, Object> annotationValues = new HashMap<String, Object>();

        Map<? extends  ExecutableElement, ? extends AnnotationValue> annotationMethods =
                elements.getElementValuesWithDefaults(annotationMirror);
        for (ExecutableElement executableElement : annotationMethods.keySet()) {
            annotationValues.put(executableElement.getSimpleName().toString(), annotationMethods.get(executableElement).getValue());
        }

        return (A) Proxy.newProxyInstance(annotationClass.getClassLoader(), new Class[] { annotationClass }, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return annotationValues.get(method.getName());
            }
        });
    }


    @Override
    public JInvocation recreateAnnotation(AnnotationMirror annotationMirror) {
        JCodeModel codeModel = CodeModelTypes.CODE_MODEL;

        Class<?> annotationClass = classOf(annotationMirror.getAnnotationType());
        JDefinedClass definedClass = codeModel.anonymousClass(annotationClass);

        Map<? extends ExecutableElement, ? extends AnnotationValue> values =
                elements.getElementValuesWithDefaults(annotationMirror);

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : values.entrySet()) {
            ExecutableElement executable = entry.getKey();
            AnnotationValue value = entry.getValue();

            JMethod definedMethod = definedClass.method(
                    JMod.PUBLIC, codeModel.ref(executable.getReturnType().toString()), executable.getSimpleName().toString());
            definedMethod.annotate(Override.class);
            if(value.getValue() instanceof List) {
                definedMethod.body()._return(JExpr.direct("new " + executable.getReturnType() + value.toString()));
            } else {
                definedMethod.body()._return(JExpr.direct(value.toString()));
            }
        }

        JMethod annotationType = definedClass.method(
                JMod.PUBLIC,
                codeModel.ref(Class.class).narrow(codeModel.ref(Annotation.class).wildcard()),
                "annotationType");
        annotationType.annotate(Override.class);
        annotationType.body()._return(codeModel.ref(annotationClass).dotclass());

        return JExpr._new(definedClass);
    }

    private DeclaredType listOf(Class<?> bound) {
        return listOf(bound != null ? typeOf(bound) : null);
    }

    private DeclaredType listOf(TypeMirror bound) {
        return environment.getTypeUtils().getDeclaredType(environment.getElementUtils().getTypeElement(
                "java.util.List"), bound);
    }

    public static class AnnotationWrapper {}


}
