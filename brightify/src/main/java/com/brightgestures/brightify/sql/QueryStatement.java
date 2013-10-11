package com.brightgestures.brightify.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public abstract class QueryStatement implements IQueryable {

    public String toSQLString() {
        StringBuilder builder = new StringBuilder();

        query(builder);

        return builder.toString();
    }

}
