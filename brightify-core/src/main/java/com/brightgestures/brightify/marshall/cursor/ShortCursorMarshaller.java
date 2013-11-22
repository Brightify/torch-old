package com.brightgestures.brightify.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import com.brightgestures.brightify.marshall.SymetricCursorMarshaller;
import com.brightgestures.brightify.sql.TypeAffinity;
import com.brightgestures.brightify.sql.affinity.IntegerAffinity;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class ShortCursorMarshaller implements SymetricCursorMarshaller<Short> {

    private static ShortCursorMarshaller instance;

    @Override
    public void marshall(ContentValues contentValues, String columnName, Short value) {
        contentValues.put(columnName, value);
    }

    @Override
    public Short unmarshall(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndexOrThrow(columnName);
        if (cursor.isNull(index)) {
            return null;
        }
        return cursor.getShort(index);
    }

    @Override
    public TypeAffinity getAffinity() {
        return IntegerAffinity.getInstance();
    }

    public static ShortCursorMarshaller getInstance() {
        if (instance == null) {
            instance = new ShortCursorMarshaller();
        }
        return instance;
    }
}
