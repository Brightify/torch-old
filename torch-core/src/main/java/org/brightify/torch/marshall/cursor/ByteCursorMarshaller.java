package org.brightify.torch.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import org.brightify.torch.marshall.SymetricCursorMarshaller;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.IntegerAffinity;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class ByteCursorMarshaller implements SymetricCursorMarshaller<Byte> {

    private static ByteCursorMarshaller instance;

    @Override
    public void marshall(ContentValues contentValues, String columnName, Byte value) {
        contentValues.put(columnName, value);
    }

    @Override
    public Byte unmarshall(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndexOrThrow(columnName);
        if(cursor.isNull(index)) {
            return null;
        }

        return (byte) cursor.getInt(index);
    }

    @Override
    public TypeAffinity getAffinity() {
        return IntegerAffinity.getInstance();
    }

    public static ByteCursorMarshaller getInstance() {
        if(instance == null) {
            instance = new ByteCursorMarshaller();
        }
        return instance;
    }
}
