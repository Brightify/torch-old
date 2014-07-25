package org.brightify.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface WritableRawEntity {

    void put(String properyName, String value);

    void put(String properyName, Byte value);

    void put(String properyName, byte value);

    void put(String properyName, Short value);

    void put(String properyName, short value);

    void put(String properyName, Integer value);

    void put(String properyName, int value);

    void put(String properyName, Long value);

    void put(String properyName, long value);

    void put(String properyName, Float value);

    void put(String properyName, float value);

    void put(String properyName, Double value);

    void put(String properyName, double value);

    void put(String properyName, Boolean value);

    void put(String properyName, boolean value);

    void put(String properyName, byte[] value);

    void putNull(String propertyName);
}
