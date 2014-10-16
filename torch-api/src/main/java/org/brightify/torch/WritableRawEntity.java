package org.brightify.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface WritableRawEntity {

    void put(String propertyName, String value);

    void put(String propertyName, Byte value);

    void put(String propertyName, byte value);

    void put(String propertyName, Short value);

    void put(String propertyName, short value);

    void put(String propertyName, Integer value);

    void put(String propertyName, int value);

    void put(String propertyName, Long value);

    void put(String propertyName, long value);

    void put(String propertyName, Float value);

    void put(String propertyName, float value);

    void put(String propertyName, Double value);

    void put(String propertyName, double value);

    void put(String propertyName, Boolean value);

    void put(String propertyName, boolean value);

    void put(String propertyName, byte[] value);

    void putNull(String propertyName);
}
