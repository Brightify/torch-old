package org.brightify.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ReadableRawEntity {

    boolean isNull(String propertyName);

    byte[] getBlob(String propertyName);

    Boolean getBoolean(String propertyName);

    boolean getBooleanPrimitive(String propertyName);

    Byte getByte(String propertyName);

    Short getShort(String propertyName);

    short getShortPrimitive(String propertyName);

    Integer getInteger(String propertyName);

    int getIntegerPrimitive(String propertyName);

    Long getLong(String propertyName);

    long getLongPrimitive(String propertyName);

    Double getDouble(String propertyName);

    double getDoublePrimitive(String propertyName);

    Float getFloat(String propertyName);

    float getFloatPrimitive(String propertyName);

    String getString(String propertyName);

}
