package org.brightify.torch.sql.statement;

import org.brightify.torch.sql.ColumnDef;
import org.brightify.torch.sql.Statement;
import org.brightify.torch.sql.constraint.TableConstraint;
import org.brightify.torch.sql.util.TextUtils;

import java.util.ArrayList;
import java.util.Collection;

public class CreateTable extends Statement {

    protected boolean temporary = false;
    protected boolean ifNotExists = true;
    protected String databaseName = null;
    protected String tableName;

    protected Collection<ColumnDef> columnDefs = new ArrayList<ColumnDef>();
    protected Collection<TableConstraint> tableConstraints = new ArrayList<TableConstraint>();

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public boolean isIfNotExists() {
        return ifNotExists;
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Collection<ColumnDef> getColumnDefs() {
        return columnDefs;
    }

    public void setColumnDefs(Collection<ColumnDef> columnDefs) {
        this.columnDefs = columnDefs;
    }

    public void addColumnDef(ColumnDef columnDef) {
        columnDefs.add(columnDef);
    }

    public Collection<TableConstraint> getTableConstraints() {
        return tableConstraints;
    }

    public void setTableConstraints(Collection<TableConstraint> tableConstraints) {
        this.tableConstraints = tableConstraints;
    }

    public void addTableConstraint(TableConstraint tableConstraint) {
        tableConstraints.add(tableConstraint);
    }

    @Override
    public void query(StringBuilder builder) {
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalStateException("Table name cannot be null or empty!");
        }

        builder.append("CREATE ");
        if (temporary) {
            builder.append("TEMP ");
        }
        builder.append("TABLE ");
        if (ifNotExists) {
            builder.append("IF NOT EXISTS ");
        }
        if (!TextUtils.isEmpty(databaseName)) {
            builder.append(databaseName).append(".");
        }
        builder
                .append(tableName)
                .append(" ");

        builder.append("(");
        int i = 0;
        for (ColumnDef columnDef : columnDefs) {
            if (i > 0) {
                builder.append(", ");
            }
            columnDef.query(builder);
            i++;
        }
        if (tableConstraints.size() > 0) {
            builder.append(" ");
            for (TableConstraint tableConstraint : tableConstraints) {
                builder.append(", ");
                tableConstraint.query(builder);
            }
        }
        builder.append(")");
    }
}
