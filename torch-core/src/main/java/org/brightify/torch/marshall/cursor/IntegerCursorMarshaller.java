package org.brightify.torch.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import org.brightify.torch.marshall.SymetricCursorMarshaller;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.IntegerAffinity;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class IntegerCursorMarshaller implements SymetricCursorMarshaller<Integer> {

    private static IntegerCursorMarshaller instance;

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

    public static IntegerCursorMarshaller getInstance() {
        if (instance == null) {
            instance = new IntegerCursorMarshaller();
        }
        return instance;
    }
}
