package org.brightify.torch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
@Target({ ElementType.METHOD })
public @interface Accessor {
    /**
     * Name of the column. Be sure it's unique in this entity!
     *
     * Use this if your getter/setter doesn't follow standard "getX()/isX()/setX(X x)".
     * Remember, compilation will fail if you won't supply both GET and SET type!
     *
     * @return Name of the accessor.
     */
    String name() default "";

    /**
     * Type of the accessor.
     *
     * @return Type of the accessor.
     */
    Type type() default Type.INFERRED;

    enum Type {
        GET, SET, INFERRED
    }

}
