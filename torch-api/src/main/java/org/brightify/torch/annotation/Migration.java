package org.brightify.torch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
public @interface Migration {

    /**
     * Source version of the Entity.
     *
     * @return The source revision of the entity.
     */
    long source();

    /**
     * Target version of the Entity.
     *
     * @return The target revision of the entity.
     */
    long target();

    /**
     * In case of equal migration path costs, path with more preferred migration methods will be selected.
     *
     * @return True if this method is preferred in case of equal cost, false otherwise.
     */
    boolean preferred() default false;
}
