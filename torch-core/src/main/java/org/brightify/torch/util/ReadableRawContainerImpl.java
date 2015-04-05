package org.brightify.torch.util;

import org.brightify.torch.ReadableRawContainer;
import org.brightify.torch.ReadableRawEntity;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ReadableRawContainerImpl implements ReadableRawContainer {

    private ReadableRawEntity rawEntity;
    private String propertyName;

    public ReadableRawEntity getRawEntity() {
        return rawEntity;
    }

    public void setRawEntity(ReadableRawEntity rawEntity) {
        this.rawEntity = rawEntity;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public boolean isNull() {
        return rawEntity.isNull(propertyName);
    }

    @Override
    public byte[] getBlob() {
        return rawEntity.getBlob(propertyName);
    }

    @Override
    public Boolean getBoolean() {
        return rawEntity.getBoolean(propertyName);
    }

    @Override
    public Byte getByte() {
        return rawEntity.getByte(propertyName);
    }

    @Override
    public Short getShort() {
        return rawEntity.getShort(propertyName);
    }

    @Override
    public Integer getInteger() {
        return rawEntity.getInteger(propertyName);
    }

    @Override
    public Long getLong() {
        return rawEntity.getLong(propertyName);
    }

    @Override
    public Double getDouble() {
        return rawEntity.getDouble(propertyName);
    }

    @Override
    public Float getFloat() {
        return rawEntity.getFloat(propertyName);
    }

    @Override
    public String getString() {
        return rawEntity.getString(propertyName);
    }
}
