package org.brightify.torch.util;

/**
 * Utility class
 *
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class Validate {

    public static void argumentNotNull(Object object, String message) throws IllegalArgumentException {
        if(object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object, String message) throws IllegalStateException {
        if(object == null) {
            throw new IllegalStateException(message);
        }
    }

    public static void isNull(Object object, String message) throws IllegalStateException {
        if(object != null) {
            throw new IllegalStateException(message);
        }
    }

    public static void instanceOf(Object object, Class<?> cls, String message) throws IllegalStateException {
        Validate.notNull(object, message);

        if(!cls.isAssignableFrom(object.getClass())) {
            throw new IllegalStateException(message);
        }
    }

}