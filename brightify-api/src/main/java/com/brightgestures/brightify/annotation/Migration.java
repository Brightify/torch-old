package com.brightgestures.brightify.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
public @interface Migration {

    /**
     * Version of Entity
     * @return
     */
    int source();

    int target();
}
