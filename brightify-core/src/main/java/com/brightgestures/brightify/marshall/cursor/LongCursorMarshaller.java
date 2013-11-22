package com.brightgestures.brightify.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import com.brightgestures.brightify.marshall.SymetricCursorMarshaller;
import com.brightgestures.brightify.sql.TypeAffinity;
import com.brightgestures.brightify.sql.affinity.IntegerAffinity;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class LongCursorMarshaller implements SymetricCursorMarshaller<Long> {

    private static LongCursorMarshaller instance;

    @Override
    public void marshall(ContentValues contentValues, String columnName, Long value) {
        contentValues.put(columnName, value);
    }

    @Override
    public Long unmarshall(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndexOrThrow(columnName);
        if (cursor.isNull(index)) {
            return null;
        }
        return cursor.getLong(index);
    }

    @Override
    public TypeAffinity getAffinity() {
        return IntegerAffinity.getInstance();
    }

    public static LongCursorMarshaller getInstance() {
        if (instance == null) {
            instance = new LongCursorMarshaller();
        }
        return instance;
    }
}
