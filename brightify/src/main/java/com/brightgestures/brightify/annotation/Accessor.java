package com.brightgestures.brightify.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Accessor {
    /**
     * Name of the column. Be sure it's unique in this entity!
     *
     * Use this if your getter/setter doesn't follow standard "getX()/isX()/setX(X x)".
     * Remember, compilation will fail if you won't supply both GET and SET type!
     */
    String name();

    /**
     * Type of the accessor.
     */
    Type type();

    enum Type {
        GET, SET
    }
}
