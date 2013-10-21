package com.brightgestures.brightify.sql;

import android.database.sqlite.SQLiteDatabase;

public abstract class Statement implements IQueryable {

    public String toSQLString() {
        StringBuilder builder = new StringBuilder();

        query(builder);

        return builder.toString();
    }

    public void run(SQLiteDatabase db) {
        String sql = toSQLString();

//        Log.d("Statement", sql);

        db.execSQL(sql);
    }
}
