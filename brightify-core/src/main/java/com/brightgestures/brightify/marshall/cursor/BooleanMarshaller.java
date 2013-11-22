package com.brightgestures.brightify.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import com.brightgestures.brightify.marshall.SymetricCursorMarshaller;
import com.brightgestures.brightify.sql.TypeAffinity;
import com.brightgestures.brightify.sql.affinity.IntegerAffinity;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class BooleanMarshaller implements SymetricCursorMarshaller<Boolean> {

    private static BooleanMarshaller instance;

    @Override
    public void marshall(ContentValues contentValues, String columnName, Boolean value) {
        contentValues.put(columnName, value);
    }

    @Override
    public Boolean unmarshall(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndexOrThrow(columnName);
        if (cursor.isNull(index)) {
            return null;
        }
        return cursor.getInt(index) > 0;
    }

    @Override
    public TypeAffinity getAffinity() {
        return IntegerAffinity.getInstance();
    }

    public static BooleanMarshaller getInstance() {
        if (instance == null) {
            instance = new BooleanMarshaller();
        }
        return instance;
    }
}
