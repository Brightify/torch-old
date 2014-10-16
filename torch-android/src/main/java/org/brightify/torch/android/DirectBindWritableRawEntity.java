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
    public void put(String propertyName, String value) {
        if(bindNull(propertyName, value)) {
            return;
        }
        statement.bindString(getIndex(propertyName), value);
    }

    @Override
    public void put(String propertyName, Byte value) {
        if(bindNull(propertyName, value)) {
            return;
        }
        put(propertyName, value.byteValue());
    }

    @Override
    public void put(String propertyName, byte value) {
        statement.bindLong(getIndex(propertyName), value);
    }

    @Override
    public void put(String propertyName, Short value) {
        if(bindNull(propertyName, value)) {
            return;
        }
        put(propertyName, value.shortValue());
    }

    @Override
    public void put(String propertyName, short value) {
        statement.bindLong(getIndex(propertyName), value);
    }

    @Override
    public void put(String propertyName, Integer value) {
        if(bindNull(propertyName, value)) {
            return;
        }
        put(propertyName, value.intValue());
    }

    @Override
    public void put(String propertyName, int value) {
        statement.bindLong(getIndex(propertyName), value);
    }

    @Override
    public void put(String propertyName, Long value) {
        if(bindNull(propertyName, value)) {
            return;
        }
        put(propertyName, value.longValue());
    }

    @Override
    public void put(String propertyName, long value) {
        statement.bindLong(getIndex(propertyName), value);
    }

    @Override
    public void put(String propertyName, Float value) {
        if(bindNull(propertyName, value)) {
            return;
        }
        put(propertyName, value.floatValue());
    }

    @Override
    public void put(String propertyName, float value) {
        statement.bindDouble(getIndex(propertyName), value);
    }

    @Override
    public void put(String propertyName, Double value) {
        if(bindNull(propertyName, value)) {
            return;
        }
        put(propertyName, value.doubleValue());
    }

    @Override
    public void put(String propertyName, double value) {
        statement.bindDouble(getIndex(propertyName), value);
    }

    @Override
    public void put(String propertyName, Boolean value) {
        if(bindNull(propertyName, value)) {
            return;
        }
        put(propertyName, value.booleanValue());
    }

    @Override
    public void put(String propertyName, boolean value) {
        statement.bindLong(getIndex(propertyName), value ? 1 : 0);
    }

    @Override
    public void put(String propertyName, byte[] value) {
        if(bindNull(propertyName, value)) {
            return;
        }
        statement.bindBlob(getIndex(propertyName), value);
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
