package com.brightgestures.brightify.sql;

import android.text.TextUtils;
import com.brightgestures.brightify.constraint.ColumnConstraint;

import java.util.ArrayList;
import java.util.Collection;

public class ColumnDef implements IQueryable {
    protected String mName;
    protected TypeName mTypeName = null;
    protected Collection<ColumnConstraint> mColumnConstraints = new ArrayList<ColumnConstraint>();

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public TypeName getTypeName() {
        return mTypeName;
    }

    public void setTypeName(TypeName typeName) {
        mTypeName = typeName;
    }

    public Collection<ColumnConstraint> getColumnConstraints() {
        return mColumnConstraints;
    }

    public void addColumnConstraint(ColumnConstraint constraint) {
        mColumnConstraints.add(constraint);
    }

    public void setColumnConstraints(Collection<ColumnConstraint> constraints) {
        mColumnConstraints = constraints;
    }

    @Override
    public void query(StringBuilder builder) {
        if(TextUtils.isEmpty(mName)) {
            throw new IllegalStateException("Name cannot be null or empty!");
        }
        builder.append(mName);
        if(mTypeName != null) {
            builder.append(" ");

            mTypeName.query(builder);
        }
        if(mColumnConstraints != null) {
            for(ColumnConstraint constraint : mColumnConstraints) {
                builder.append(" ");
                constraint.query(builder);
            }
        }
    }
}
