package org.brightify.torch.annotation;

import org.brightify.torch.filter.Property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Id {
    boolean autoIncrement() default true;

    public static class IdFeature implements Property.Feature {
        private final boolean autoIncrement;

        public IdFeature(boolean autoIncrement) {
            this.autoIncrement = autoIncrement;
        }

        public boolean isAutoIncrement() {
            return autoIncrement;
        }
    }
}
