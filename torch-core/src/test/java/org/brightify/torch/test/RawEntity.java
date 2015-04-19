package org.brightify.torch.test;

import com.google.common.base.MoreObjects;
import org.brightify.torch.ReadableRawEntity;
import org.brightify.torch.WritableRawEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class RawEntity implements ReadableRawEntity, WritableRawEntity {

    private Map<String, Object> values = new HashMap<String, Object>();

    @Override
    public boolean isNull(String propertyName) {
        return values.containsKey(propertyName);
    }

    @Override
    public byte[] getBlob(String propertyName) {
        return getValue(propertyName);
    }

    @Override
    public Boolean getBoolean(String propertyName) {
        return getValue(propertyName);
    }

    @Override
    public boolean getBooleanPrimitive(String propertyName) {
        return getValue(propertyName);
    }

    @Override
    public Byte getByte(String propertyName) {
        return getValue(propertyName);
    }

    @Override
    public Short getShort(String propertyName) {
        return getValue(propertyName);
    }

    @Override
    public short getShortPrimitive(String propertyName) {
        return getValue(propertyName);
    }

    @Override
    public Integer getInteger(String propertyName) {
        return getValue(propertyName);
    }

    @Override
    public int getIntegerPrimitive(String propertyName) {
        return getValue(propertyName);
    }

    @Override
    public Long getLong(String propertyName) {
        return getValue(propertyName);
    }

    @Override
    public long getLongPrimitive(String propertyName) {
        return getValue(propertyName);
    }

    @Override
    public Double getDouble(String propertyName) {
        return getValue(propertyName);
    }

    @Override
    public double getDoublePrimitive(String propertyName) {
        return getValue(propertyName);
    }

    @Override
    public Float getFloat(String propertyName) {
        return getValue(propertyName);
    }

    @Override
    public float getFloatPrimitive(String propertyName) {
        return getValue(propertyName);
    }

    @Override
    public String getString(String propertyName) {
        return getValue(propertyName);
    }

    @Override
    public void put(String propertyName, String value) {
        storeValue(propertyName, value);
    }

    @Override
    public void put(String propertyName, Byte value) {
        storeValue(propertyName, value);
    }

    @Override
    public void put(String propertyName, byte value) {
        storeValue(propertyName, value);
    }

    @Override
    public void put(String propertyName, Short value) {
        storeValue(propertyName, value);
    }

    @Override
    public void put(String propertyName, short value) {
        storeValue(propertyName, value);
    }

    @Override
    public void put(String propertyName, Integer value) {
        storeValue(propertyName, value);
    }

    @Override
    public void put(String propertyName, int value) {
        storeValue(propertyName, value);
    }

    @Override
    public void put(String propertyName, Long value) {
        storeValue(propertyName, value);
    }

    @Override
    public void put(String propertyName, long value) {
        storeValue(propertyName, value);
    }

    @Override
    public void put(String propertyName, Float value) {
        storeValue(propertyName, value);
    }

    @Override
    public void put(String propertyName, float value) {
        storeValue(propertyName, value);
    }

    @Override
    public void put(String propertyName, Double value) {
        storeValue(propertyName, value);
    }

    @Override
    public void put(String propertyName, double value) {
        storeValue(propertyName, value);
    }

    @Override
    public void put(String propertyName, Boolean value) {
        storeValue(propertyName, value);
    }

    @Override
    public void put(String propertyName, boolean value) {
        storeValue(propertyName, value);
    }

    @Override
    public void put(String propertyName, byte[] value) {
        storeValue(propertyName, value);
    }

    @Override
    public void putNull(String propertyName) {
        storeValue(propertyName, null);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getValue(String propertyName) {
        return (T) values.get(propertyName);
    }

    protected <T> void storeValue(String propertyName, T value) {
        values.put(propertyName, value);
    }

    public static class PrimaryKey {

        private final Object[] parts;

        public PrimaryKey(Object... parts) {
            this.parts = parts;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PrimaryKey that = (PrimaryKey) o;
            return Arrays.equals(parts, that.parts);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(parts);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                              .add("parts", Arrays.deepToString(parts))
                              .toString();
        }

        public static PrimaryKey of(Object... parts) {
            return new PrimaryKey(parts);
        }
    }

}
