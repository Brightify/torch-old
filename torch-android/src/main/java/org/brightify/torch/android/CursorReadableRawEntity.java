package org.brightify.torch.android;

import android.database.Cursor;
import org.brightify.torch.ReadableRawEntity;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class CursorReadableRawEntity implements ReadableRawEntity {

    private final Cursor cursor;

    public CursorReadableRawEntity(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public boolean isNull(String propertyName) {
        return cursor.isNull(getIndex(propertyName));
    }

    @Override
    public byte[] getBlob(String propertyName) {
        return cursor.getBlob(getIndex(propertyName));
    }

    @Override
    public Boolean getBoolean(String propertyName) {
        int index = getIndex(propertyName);
        if(cursor.isNull(index)) {
            return null;
        }
        return cursor.getInt(index) > 0;
    }

    @Override
    public boolean getBooleanPrimitive(String propertyName) {
        return cursor.getInt(getIndex(propertyName)) > 0;
    }

    @Override
    public Short getShort(String propertyName) {
        int index = getIndex(propertyName);
        if(cursor.isNull(index)) {
            return null;
        }
        return cursor.getShort(index);
    }

    @Override
    public short getShortPrimitive(String propertyName) {
        return cursor.getShort(getIndex(propertyName));
    }

    @Override
    public Integer getInteger(String propertyName) {
        int index = getIndex(propertyName);
        if(cursor.isNull(index)) {
            return null;
        }
        return cursor.getInt(index);
    }

    @Override
    public int getIntegerPrimitive(String propertyName) {
        return cursor.getInt(getIndex(propertyName));
    }

    @Override
    public Long getLong(String propertyName) {
        int index = getIndex(propertyName);
        if(cursor.isNull(index)) {
            return null;
        }
        return cursor.getLong(index);
    }

    @Override
    public long getLongPrimitive(String propertyName) {
        return cursor.getLong(getIndex(propertyName));
    }

    @Override
    public Double getDouble(String propertyName) {
        int index = getIndex(propertyName);
        if(cursor.isNull(index)) {
            return null;
        }
        return cursor.getDouble(index);
    }

    @Override
    public double getDoublePrimitive(String propertyName) {
        return cursor.getDouble(getIndex(propertyName));
    }

    @Override
    public Float getFloat(String propertyName) {
        int index = getIndex(propertyName);
        if(cursor.isNull(index)) {
            return null;
        }
        return cursor.getFloat(index);
    }

    @Override
    public float getFloatPrimitive(String propertyName) {
        return cursor.getFloat(getIndex(propertyName));
    }

    @Override
    public String getString(String propertyName) {
        return cursor.getString(getIndex(propertyName));
    }

    private int getIndex(String propertyName) {
        return cursor.getColumnIndexOrThrow(propertyName);
    }
}
