package org.brightify.torch.annotation;

import org.brightify.torch.filter.Property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Load {
    Class<?>[] value() default { };

    Class<?>[] unless() default { };

    class LoadFeature implements Property.Feature {
        private final Class<?>[] value;

        private final Class<?>[] unless;

        public LoadFeature(Class<?>[] value, Class<?>[] unless) {
            this.value = value;
            this.unless = unless;
        }

        public Class<?>[] getValue() {
            return value;
        }

        public Class<?>[] getUnless() {
            return unless;
        }
    }
}
