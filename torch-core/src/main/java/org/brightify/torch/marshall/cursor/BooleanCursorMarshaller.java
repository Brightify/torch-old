package org.brightify.torch.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import org.brightify.torch.marshall.SymetricCursorMarshaller;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.IntegerAffinity;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class BooleanCursorMarshaller implements SymetricCursorMarshaller<Boolean> {

    private static BooleanCursorMarshaller instance;

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

    public static BooleanCursorMarshaller getInstance() {
        if (instance == null) {
            instance = new BooleanCursorMarshaller();
        }
        return instance;
    }
}
