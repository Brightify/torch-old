package org.brightify.torch.annotation;

import org.brightify.torch.filter.Property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Unique {

    class UniqueFeature implements Property.Feature { }

}
