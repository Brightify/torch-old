package org.brightify.torch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
public @interface Migration {

    /**
     * Source version of the Entity.
     * @return
     */
    String source();

    /**
     * Target version of the Entity.
     * @return
     */
    String target();

    /**
     * In case of equal migration path costs, path with more preferred migration methods will be selected.
     * @return
     */
    boolean preferred() default false;
}
