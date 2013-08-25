package com.brightgestures.droidorm.sql;

public abstract class TypeName implements IQueryable {
    public abstract String getName();

    @Override
    public void query(StringBuilder builder) {
        builder.append(getName());
    }
}
