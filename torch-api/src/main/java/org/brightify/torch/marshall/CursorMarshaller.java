package org.brightify.torch.marshall;

import android.content.ContentValues;
import android.database.Cursor;
import org.brightify.torch.sql.TypeAffinity;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface CursorMarshaller<INPUT_TYPE, OUTPUT_TYPE extends INPUT_TYPE> {

    void marshall(ContentValues contentValues, String columnName, INPUT_TYPE value) throws Exception;

    OUTPUT_TYPE unmarshall(Cursor cursor, String columnName) throws Exception;

    TypeAffinity getAffinity();

}
