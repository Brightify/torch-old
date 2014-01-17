package com.brightgestures.brightify.filter;

import com.brightgestures.brightify.action.load.EntityFilter;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class ColumnImpl<T> implements Column<T> {

    private final String columnName;
    private final Class<T> columnType;

    public ColumnImpl(String columName, Class<T> columnType) {
        this.columnName = columName;
        this.columnType = columnType;
    }

    @Override
    public String getName() {
        return columnName;
    }

    @Override
    public Class<T> getType() {
        return columnType;
    }

    @Override
    public EntityFilter equalTo(T value) {
        return EntityFilter.filter(getName() + " = ?", value);
    }

    @Override
    public EntityFilter notEqualTo(T value) {
        return EntityFilter.filter(getName() + " != ?", value);
    }

    @Override
    public EntityFilter in(T... values) {
        StringBuilder builder = new StringBuilder(getName());
        builder.append(" IN (");
        for(int i = 0; i < values.length; i++) {
            if(i > 0) {
                builder.append(", ");
            }
            builder.append("?");
        }
        builder.append(")");
        return EntityFilter.filter(builder.toString(), values);
    }

    @Override
    public EntityFilter notIn(T... values) {
        StringBuilder builder = new StringBuilder(getName());
        builder.append(" NOT IN (");
        for(int i = 0; i < values.length; i++) {
            if(i>0) {
                builder.append(", ");
            }
            builder.append("?");
        }
        builder.append(")");
        return EntityFilter.filter(builder.toString(), values);
    }


}
