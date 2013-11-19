package com.brightgestures.brightify.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import com.brightgestures.brightify.marshall.CursorMarshaller;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class ByteMarshaller implements CursorMarshaller<Byte> {

    @Override
    public void marshall(ContentValues contentValues, String columnName, Byte value) {
        contentValues.put(columnName, value);
    }

    @Override
    public Byte unmarshall(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if(cursor.isNull(index)) {
            return null;
        }

        return (byte) cursor.getInt(index);
    }
}
