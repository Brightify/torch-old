package org.brightify.torch.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import org.brightify.torch.marshall.SymetricCursorMarshaller;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.IntegerAffinity;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
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
