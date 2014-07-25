package org.brightify.torch.compile.util;

import com.sun.codemodel.JInvocation;
import org.brightify.torch.compile.PropertyMirror;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface TypeHelper {
    ProcessingEnvironment getProcessingEnvironment();

    TypeMirror getWrappedType(PropertyMirror propertyMirror);

    TypeMirror getWrappedType(TypeMirror propertyType);

    TypeElement elementOf(Class<?> cls);

    TypeMirror typeOf(Class<?> cls);

    Class<?> classOf(TypeMirror mirror);

    String packageOf(Element element);

    Map<TypeVariable<?>, Type> genericParameters(Class<?> targetClass, Class<?> cls, ParameterizedType type);

    List<? extends TypeMirror> genericParameters(TypeMirror type);

    TypeMirror singleGenericParameter(TypeMirror type);

    <A extends Annotation> A getAnnotation(List<? extends AnnotationMirror> annotationMirrors,
                                           Class<A> annotationClass);

    JInvocation recreateAnnotation(AnnotationMirror annotationMirror);
}
