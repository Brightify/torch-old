package org.brightify.torch.android;

import android.database.sqlite.SQLiteStatement;
import org.brightify.torch.WritableRawEntity;

import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class DirectBindWritableRawEntity implements WritableRawEntity {

    private final SQLiteStatement statement;
    private final Map<String, Integer> bindArgIndexes;

    public DirectBindWritableRawEntity(AndroidSQLiteEngine.CompiledStatement statement) {
        this.statement = statement.getSQLiteStatement();
        this.bindArgIndexes = statement.getBindArgIndexes();
    }

    @Override
    public void put(String properyName, String value) {
        if(bindNull(properyName, value)) {
            return;
        }
        statement.bindString(getIndex(properyName), value);
    }

    @Override
    public void put(String properyName, Byte value) {
        if(bindNull(properyName, value)) {
            return;
        }
        put(properyName, value.byteValue());
    }

    @Override
    public void put(String properyName, byte value) {
        statement.bindLong(getIndex(properyName), value);
    }

    @Override
    public void put(String properyName, Short value) {
        if(bindNull(properyName, value)) {
            return;
        }
        put(properyName, value.shortValue());
    }

    @Override
    public void put(String properyName, short value) {
        statement.bindLong(getIndex(properyName), value);
    }

    @Override
    public void put(String properyName, Integer value) {
        if(bindNull(properyName, value)) {
            return;
        }
        put(properyName, value.intValue());
    }

    @Override
    public void put(String properyName, int value) {
        statement.bindLong(getIndex(properyName), value);
    }

    @Override
    public void put(String properyName, Long value) {
        if(bindNull(properyName, value)) {
            return;
        }
        put(properyName, value.longValue());
    }

    @Override
    public void put(String properyName, long value) {
        statement.bindLong(getIndex(properyName), value);
    }

    @Override
    public void put(String properyName, Float value) {
        if(bindNull(properyName, value)) {
            return;
        }
        put(properyName, value.floatValue());
    }

    @Override
    public void put(String properyName, float value) {
        statement.bindDouble(getIndex(properyName), value);
    }

    @Override
    public void put(String properyName, Double value) {
        if(bindNull(properyName, value)) {
            return;
        }
        put(properyName, value.doubleValue());
    }

    @Override
    public void put(String properyName, double value) {
        statement.bindDouble(getIndex(properyName), value);
    }

    @Override
    public void put(String properyName, Boolean value) {
        if(bindNull(properyName, value)) {
            return;
        }
        put(properyName, value.booleanValue());
    }

    @Override
    public void put(String properyName, boolean value) {
        statement.bindLong(getIndex(properyName), value ? 1 : 0);
    }

    @Override
    public void put(String properyName, byte[] value) {
        if(bindNull(properyName, value)) {
            return;
        }
        statement.bindBlob(getIndex(properyName), value);
    }

    @Override
    public void putNull(String propertyName) {
        statement.bindNull(getIndex(propertyName));
    }

    private boolean bindNull(String propertyName, Object value) {
        if(value == null) {
            putNull(propertyName);
            return true;
        }
        return false;
    }

    public void clear() {
        statement.clearBindings();
    }

    private int getIndex(String propertyName) {
        return bindArgIndexes.get(propertyName);
    }

}
