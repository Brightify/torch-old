package org.brightify.torch.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import org.brightify.torch.marshall.SymetricCursorMarshaller;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.TextAffinity;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class StringCursorMarshaller implements SymetricCursorMarshaller<String> {

    private static StringCursorMarshaller instance;

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

    public static StringCursorMarshaller getInstance() {
        if (instance == null) {
            instance = new StringCursorMarshaller();
        }
        return instance;
    }
}
