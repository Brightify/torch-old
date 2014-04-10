package org.brightify.torch.sql;

import org.brightify.torch.sql.util.TextUtils;

public class IndexedColumn implements SqlQueryPart {

    protected String columnName;
    protected String collationName = null;
    protected Direction direction = null;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getCollationName() {
        return collationName;
    }

    public void setCollationName(String collationName) {
        this.collationName = collationName;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void query(StringBuilder builder) {
        if(TextUtils.isEmpty(columnName)) {
            throw new IllegalStateException("Column name cannot be null or empty!");
        }

        builder.append(columnName);
        if(collationName != null) {
            builder.append(" ").append(collationName);
        }
        if(direction != null) {
            builder.append(" ").append(direction.name());
        }
    }

    public enum Direction {
        ASC, DESC
    }
}
