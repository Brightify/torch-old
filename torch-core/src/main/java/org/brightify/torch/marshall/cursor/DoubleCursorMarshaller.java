package org.brightify.torch.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import org.brightify.torch.marshall.SymetricCursorMarshaller;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.RealAffinity;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class DoubleCursorMarshaller implements SymetricCursorMarshaller<Double> {

    private static DoubleCursorMarshaller instance;

    @Override
    public void marshall(ContentValues contentValues, String columnName, Double value) {
        contentValues.put(columnName, value);
    }

    @Override
    public Double unmarshall(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndexOrThrow(columnName);
        if (cursor.isNull(index)) {
            return null;
        }
        return cursor.getDouble(index);
    }

    @Override
    public TypeAffinity getAffinity() {
        return RealAffinity.getInstance();
    }

    public static DoubleCursorMarshaller getInstance() {
        if (instance == null) {
            instance = new DoubleCursorMarshaller();
        }
        return instance;
    }
}
