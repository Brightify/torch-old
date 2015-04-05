package org.brightify.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface WritableRawContainer {

    void put(String value);

    void put(Byte value);

    void put(Short value);

    void put(Integer value);

    void put(Long value);

    void put(Float value);

    void put(Double value);

    void put(Boolean value);

    void put(byte[] value);

    void putNull();
}
