package com.brightgestures.brightify.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import com.brightgestures.brightify.marshall.SymetricCursorMarshaller;
import com.brightgestures.brightify.sql.TypeAffinity;
import com.brightgestures.brightify.sql.affinity.IntegerAffinity;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class IntegerMarshaller implements SymetricCursorMarshaller<Integer> {

    private static IntegerMarshaller instance;

    @Override
    public void marshall(ContentValues contentValues, String columnName, Integer value) {
        contentValues.put(columnName, value);
    }

    @Override
    public Integer unmarshall(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndexOrThrow(columnName);
        if (cursor.isNull(index)) {
            return null;
        }
        return cursor.getInt(index);
    }

    @Override
    public TypeAffinity getAffinity() {
        return IntegerAffinity.getInstance();
    }

    public static IntegerMarshaller getInstance() {
        if (instance == null) {
            instance = new IntegerMarshaller();
        }
        return instance;
    }
}
