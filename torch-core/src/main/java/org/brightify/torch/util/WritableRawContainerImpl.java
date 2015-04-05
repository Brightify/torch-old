package org.brightify.torch.util;

import org.brightify.torch.WritableRawContainer;
import org.brightify.torch.WritableRawEntity;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class WritableRawContainerImpl implements WritableRawContainer {

    private WritableRawEntity rawEntity;
    private String propertyName;

    public WritableRawEntity getRawEntity() {
        return rawEntity;
    }

    public void setRawEntity(WritableRawEntity rawEntity) {
        this.rawEntity = rawEntity;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public void put(String value) {
        rawEntity.put(propertyName, value);
    }

    @Override
    public void put(Byte value) {
        rawEntity.put(propertyName, value);
    }

    @Override
    public void put(Short value) {
        rawEntity.put(propertyName, value);
    }

    @Override
    public void put(Integer value) {
        rawEntity.put(propertyName, value);
    }

    @Override
    public void put(Long value) {
        rawEntity.put(propertyName, value);
    }

    @Override
    public void put(Float value) {
        rawEntity.put(propertyName, value);
    }

    @Override
    public void put(Double value) {
        rawEntity.put(propertyName, value);
    }

    @Override
    public void put(Boolean value) {
        rawEntity.put(propertyName, value);
    }

    @Override
    public void put(byte[] value) {
        rawEntity.put(propertyName, value);
    }

    @Override
    public void putNull() {
        rawEntity.putNull(propertyName);
    }
}
