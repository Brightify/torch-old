package org.brightify.torch.sql;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.brightify.torch.Settings;
import org.brightify.torch.sql.SqlQueryPart;

public abstract class Statement implements SqlQueryPart {

    public String toSQLString() {
        StringBuilder builder = new StringBuilder();

        query(builder);

        return builder.toString();
    }

    public void run(SQLiteDatabase db) {
        String sql = toSQLString();

        if(Settings.isQueryLoggingEnabled()) {
            Log.d(Statement.class.getSimpleName(), sql);
        }

        db.execSQL(sql);
    }
}
