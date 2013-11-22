package com.brightgestures.brightify.sql;

public abstract class AbstractTypeAffinity implements TypeAffinity {
    @Override
    public void query(StringBuilder builder) {
        builder.append(getName());
    }
}
