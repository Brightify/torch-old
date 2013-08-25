package com.brightgestures.droidorm.sql;

import android.text.TextUtils;

public class IndexedColumn implements IQueryable {

    protected String mColumnName;
    protected String mCollationName = null;
    protected Direction mDirection = null;

    public String getColumnName() {
        return mColumnName;
    }

    public void setColumnName(String columnName) {
        mColumnName = columnName;
    }

    public String getCollationName() {
        return mCollationName;
    }

    public void setCollationName(String collationName) {
        mCollationName = collationName;
    }

    public Direction getDirection() {
        return mDirection;
    }

    public void setDirection(Direction direction) {
        mDirection = direction;
    }

    @Override
    public void query(StringBuilder builder) {
        if(TextUtils.isEmpty(mColumnName)) {
            throw new IllegalStateException("Column name cannot be null or empty!");
        }

        builder.append(mColumnName);
        if(mCollationName != null) {
            builder.append(" ").append(mCollationName);
        }
        if(mDirection != null) {
            builder.append(" ").append(mDirection.name());
        }
    }

    public enum Direction {
        ASC, DESC
    }
}
