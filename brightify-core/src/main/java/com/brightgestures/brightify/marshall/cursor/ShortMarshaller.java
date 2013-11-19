package com.brightgestures.brightify.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import com.brightgestures.brightify.marshall.CursorMarshaller;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class ShortMarshaller implements CursorMarshaller<Short> {

    @Override
    public void marshall(ContentValues contentValues, String columnName, Short value) {
        contentValues.put(columnName, value);
    }

    @Override
    public Short unmarshall(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndexOrThrow(columnName);
        if(cursor.isNull(index)) {
            return null;
        }
        return cursor.getShort(index);
    }
}
