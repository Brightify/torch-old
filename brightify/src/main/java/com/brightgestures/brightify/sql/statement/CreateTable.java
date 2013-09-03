package com.brightgestures.brightify.sql.statement;

import android.text.TextUtils;
import com.brightgestures.brightify.sql.ColumnDef;
import com.brightgestures.brightify.sql.Statement;
import com.brightgestures.brightify.constraint.TableConstraint;

import java.util.ArrayList;
import java.util.Collection;

public class CreateTable extends Statement {

    protected boolean mTemporary = false;
    protected boolean mIfNotExists = true;
    protected String mDatabaseName = null;
    protected String mTableName;

    protected Collection<ColumnDef> mColumnDefs = new ArrayList<ColumnDef>();
    protected Collection<TableConstraint> mTableConstraints = new ArrayList<TableConstraint>();

    public boolean isTemporary() {
        return mTemporary;
    }

    public void setTemporary(boolean temporary) {
        mTemporary = temporary;
    }

    public boolean isIfNotExists() {
        return mIfNotExists;
    }

    public void setIfNotExists(boolean ifNotExists) {
        mIfNotExists = ifNotExists;
    }

    public String getDatabaseName() {
        return mDatabaseName;
    }

    public void setDatabaseName(String databaseName) {
        mDatabaseName = databaseName;
    }

    public String getTableName() {
        return mTableName;
    }

    public void setTableName(String tableName) {
        mTableName = tableName;
    }

    public Collection<ColumnDef> getColumnDefs() {
        return mColumnDefs;
    }

    public void addColumnDef(ColumnDef columnDef) {
        mColumnDefs.add(columnDef);
    }

    public void setColumnDefs(Collection<ColumnDef> columnDefs) {
        mColumnDefs = columnDefs;
    }

    public Collection<TableConstraint> getTableConstraints() {
        return mTableConstraints;
    }

    public void addTableConstraint(TableConstraint tableConstraint) {
        mTableConstraints.add(tableConstraint);
    }

    public void setTableConstraints(Collection<TableConstraint> tableConstraints) {
        mTableConstraints = tableConstraints;
    }

    @Override
    public void query(StringBuilder builder) {
        if(TextUtils.isEmpty(mTableName)) {
            throw new IllegalStateException("Table name cannot be null or empty!");
        }

        builder.append("CREATE ");
        if(mTemporary) {
            builder.append("TEMP ");
        }
        builder.append("TABLE ");
        if(mIfNotExists) {
            builder.append("IF NOT EXISTS ");
        }
        if(!TextUtils.isEmpty(mDatabaseName)) {
            builder.append(mDatabaseName).append(".");
        }
        builder
            .append(mTableName)
            .append(" ");

        builder.append("(");
        int i = 0;
        for(ColumnDef columnDef : mColumnDefs) {
            if(i > 0) {
                builder.append(", ");
            }
            columnDef.query(builder);
            i++;
        }
        if(mTableConstraints.size() > 0) {
            builder.append(" ");
            for(TableConstraint tableConstraint : mTableConstraints) {
                builder.append(", ");
                tableConstraint.query(builder);
            }
        }
        builder.append(")");
    }
}
