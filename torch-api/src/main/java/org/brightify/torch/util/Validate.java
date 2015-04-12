package org.brightify.torch.util;

import java.text.MessageFormat;

/**
 * Utility class
 *
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class Validate {

    public static void argumentNotNull(Object object, String message, Object... params)
            throws IllegalArgumentException {
        if(object == null) {
            throw new IllegalArgumentException(MessageFormat.format(message, params));
        }
    }

    public static void notNull(Object object, String message, Object... params) throws IllegalStateException {
        if(object == null) {
            throw new IllegalStateException(MessageFormat.format(message, params));
        }
    }

    public static void isNull(Object object, String message, Object... params) throws IllegalStateException {
        if(object != null) {
            throw new IllegalStateException(MessageFormat.format(message, params));
        }
    }

    public static void isTrue(boolean value, String message, Object... params) throws IllegalStateException {
        if(!value) {
            throw new IllegalStateException(MessageFormat.format(message, params));
        }
    }

    public static void instanceOf(Object object, Class<?> cls, String message, Object... params)
            throws IllegalStateException {
        Validate.notNull(object, message, params);

        if(!cls.isAssignableFrom(object.getClass())) {
            throw new IllegalStateException(MessageFormat.format(message, params));
        }
    }

}