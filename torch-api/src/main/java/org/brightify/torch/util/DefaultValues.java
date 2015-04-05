package org.brightify.torch.util;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class DefaultValues {

    private static boolean DEFAULT_BOOLEAN;
    private static byte DEFAULT_BYTE;
    private static short DEFAULT_SHORT;
    private static int DEFAULT_INT;
    private static long DEFAULT_LONG;
    private static float DEFAULT_FLOAT;
    private static double DEFAULT_DOUBLE;

    public static <T> T forType(Class<T> type) {
        if(type.equals(boolean.class)) {
            return type.cast(DEFAULT_BOOLEAN);
        } else if(type.equals(byte.class)) {
            return type.cast(DEFAULT_DOUBLE);
        }
        Object o = 10;

        return null;


    }
}
