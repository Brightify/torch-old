package org.brightify.torch.sql;

import android.text.TextUtils;
import org.brightify.torch.sql.constraint.ColumnConstraint;

import java.util.ArrayList;
import java.util.Collection;

public class ColumnDef implements SqlQueryPart {
    protected String mName;
    protected TypeAffinity typeAffinity = null;
    protected Collection<ColumnConstraint> mColumnConstraints = new ArrayList<ColumnConstraint>();

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public TypeAffinity getTypeAffinity() {
        return typeAffinity;
    }

    public void setTypeAffinity(TypeAffinity typeAffinity) {
        this.typeAffinity = typeAffinity;
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
        if(typeAffinity != null) {
            builder.append(" ");

            typeAffinity.query(builder);
        }
        if(mColumnConstraints != null) {
            for(ColumnConstraint constraint : mColumnConstraints) {
                builder.append(" ");
                constraint.query(builder);
            }
        }
    }
}
