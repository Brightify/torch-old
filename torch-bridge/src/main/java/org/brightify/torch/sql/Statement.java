package org.brightify.torch.sql;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.brightify.torch.sql.SqlQueryPart;

public abstract class Statement implements SqlQueryPart {

    public String toSQLString() {
        StringBuilder builder = new StringBuilder();

        query(builder);

        return builder.toString();
    }

    public void run(SQLiteDatabase db) {
        String sql = toSQLString();

        Log.d("Statement", sql);

        db.execSQL(sql);
    }
}
