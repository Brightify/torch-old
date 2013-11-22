package com.brightgestures.brightify.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import com.brightgestures.brightify.marshall.SymetricCursorMarshaller;
import com.brightgestures.brightify.sql.TypeAffinity;
import com.brightgestures.brightify.sql.affinity.TextAffinity;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class StringMarshaller implements SymetricCursorMarshaller<String> {

    private static StringMarshaller instance;

    @Override
    public void marshall(ContentValues contentValues, String columnName, String value) {
        contentValues.put(columnName, value);
    }

    @Override
    public String unmarshall(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndexOrThrow(columnName);
        // FIXME do we need to do the Cursor#isNull check?
        return cursor.getString(index);
    }

    @Override
    public TypeAffinity getAffinity() {
        return TextAffinity.getInstance();
    }

    public static StringMarshaller getInstance() {
        if (instance == null) {
            instance = new StringMarshaller();
        }
        return instance;
    }
}
