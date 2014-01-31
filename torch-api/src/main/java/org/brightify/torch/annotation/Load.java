package org.brightify.torch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Load {
    Class<?>[] value() default { };

    Class<?>[] unless() default { };
}
