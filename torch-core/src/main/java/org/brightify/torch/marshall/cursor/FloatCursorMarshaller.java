package org.brightify.torch.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import org.brightify.torch.marshall.SymetricCursorMarshaller;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.IntegerAffinity;
import org.brightify.torch.sql.affinity.RealAffinity;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class FloatCursorMarshaller implements SymetricCursorMarshaller<Float> {

    private static FloatCursorMarshaller instance;

    @Override
    public void marshall(ContentValues contentValues, String columnName, Float value) {
        contentValues.put(columnName, value);
    }

    @Override
    public Float unmarshall(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndexOrThrow(columnName);
        if (cursor.isNull(index)) {
            return null;
        }
        return cursor.getFloat(index);
    }

    @Override
    public TypeAffinity getAffinity() {
        return RealAffinity.getInstance();
    }

    public static FloatCursorMarshaller getInstance() {
        if (instance == null) {
            instance = new FloatCursorMarshaller();
        }
        return instance;
    }
}
