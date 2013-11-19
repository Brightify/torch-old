package com.brightgestures.brightify.marshall;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface CursorMarshaller<T> {

    void marshall(ContentValues contentValues, String columnName, T value);

    T unmarshall(Cursor cursor, String columnName);

}
