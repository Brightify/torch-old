package org.brightify.torch.sql;

import org.brightify.torch.sql.constraint.ColumnConstraint;
import org.brightify.torch.sql.util.TextUtils;

import java.util.ArrayList;
import java.util.Collection;

public class ColumnDef implements SqlQueryPart {
    protected final String name;
    protected TypeAffinity typeAffinity = null;
    protected Collection<ColumnConstraint> columnConstraints = new ArrayList<ColumnConstraint>();

    public ColumnDef(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public TypeAffinity getTypeAffinity() {
        return typeAffinity;
    }

    public void setTypeAffinity(TypeAffinity typeAffinity) {
        this.typeAffinity = typeAffinity;
    }

    public Collection<ColumnConstraint> getColumnConstraints() {
        return columnConstraints;
    }

    public void setColumnConstraints(Collection<ColumnConstraint> constraints) {
        for (ColumnConstraint constraint : constraints) {
            constraint.setColumnName(getName());
        }
        columnConstraints = constraints;
    }

    public void addColumnConstraint(ColumnConstraint constraint) {
        constraint.setColumnName(getName());
        columnConstraints.add(constraint);
    }

    @Override
    public void query(StringBuilder builder) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalStateException("Name cannot be null or empty!");
        }
        builder.append(name);
        if (typeAffinity != null) {
            builder.append(" ");

            typeAffinity.query(builder);
        }
        if (columnConstraints != null) {
            for (ColumnConstraint constraint : columnConstraints) {
                builder.append(" ");
                constraint.query(builder);
            }
        }
    }
}
