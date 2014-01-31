package org.brightify.torch.util;

import java.lang.annotation.Annotation;
import java.util.HashMap;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class AnnotationMap extends HashMap<Class<? extends Annotation>, Annotation> {
    @SuppressWarnings("unchecked")
    public final <A extends Annotation> A getAnnotation(Class<A> cls) {
        return (A) super.get(cls);
    }

    @SuppressWarnings("unchecked")
    public final <A extends Annotation> A putAnnotation(A value) {
        return (A) super.put(value.annotationType(), value);
    }
}
