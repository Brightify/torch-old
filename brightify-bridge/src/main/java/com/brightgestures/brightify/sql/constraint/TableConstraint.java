package com.brightgestures.brightify.sql.constraint;

import com.brightgestures.brightify.sql.SqlQueryPart;
import com.brightgestures.brightify.sql.clause.ConflictClause;
import com.brightgestures.brightify.sql.clause.ForeignKeyClause;
import com.brightgestures.brightify.sql.IndexedColumn;

import java.util.ArrayList;
import java.util.Collection;

public abstract class TableConstraint implements SqlQueryPart {

    protected String mName = null;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public void query(StringBuilder builder) {
        if(mName != null) {
            builder.append("CONSTRAINT ").append(mName).append(" ");
        }
    }

    public static class PrimaryKey extends TableConstraint {

        protected Collection<IndexedColumn> mIndexedColumns = new ArrayList<IndexedColumn>();
        protected ConflictClause mConflictClause = new ConflictClause();

        public Collection<IndexedColumn> getIndexedColumns() {
            return mIndexedColumns;
        }

        public void addIndexedColumn(IndexedColumn column) {
            mIndexedColumns.add(column);
        }

        public void setIndexedColumns(Collection<IndexedColumn> indexedColumns) {
            mIndexedColumns = indexedColumns;
        }

        public ConflictClause getConflictClause() {
            return mConflictClause;
        }

        public void setConflictClause(ConflictClause conflictClause) {
            mConflictClause = conflictClause;
        }

        @Override
        public void query(StringBuilder builder) {
            super.query(builder);

            builder.append("PRIMARY KEY (");
            int i = 0;
            for(IndexedColumn column : mIndexedColumns) {
                if(i > 0) {
                    builder.append(", ");
                }
                column.query(builder);
                i++;
            }
            builder.append(") ");

            mConflictClause.query(builder);
        }
    }


    public static class Unique extends TableConstraint {

        protected Collection<IndexedColumn> mIndexedColumns = new ArrayList<IndexedColumn>();
        protected ConflictClause mConflictClause = new ConflictClause();

        public Collection<IndexedColumn> getIndexedColumns() {
            return mIndexedColumns;
        }

        public void addIndexedColumn(IndexedColumn column) {
            mIndexedColumns.add(column);
        }

        public void setIndexedColumns(Collection<IndexedColumn> indexedColumns) {
            mIndexedColumns = indexedColumns;
        }

        public ConflictClause getConflictClause() {
            return mConflictClause;
        }

        public void setConflictClause(ConflictClause conflictClause) {
            mConflictClause = conflictClause;
        }

        @Override
        public void query(StringBuilder builder) {
            super.query(builder);

            builder.append("UNIQUE (");
            int i = 0;
            for(IndexedColumn column : mIndexedColumns) {
                if(i > 0) {
                    builder.append(", ");
                }
                column.query(builder);
                i++;
            }
            builder.append(") ");

            mConflictClause.query(builder);
        }
    }

    public class ForeignKey extends TableConstraint {

        protected Collection<String> mColumnNames = new ArrayList<String>();
        protected ForeignKeyClause mForeignKeyClause = new ForeignKeyClause();

        public Collection<String> getColumnNames() {
            return mColumnNames;
        }

        public void addColumnName(String columnName) {
            mColumnNames.add(columnName);
        }

        public void setColumnNames(Collection<String> columnNames) {
            mColumnNames = columnNames;
        }

        @Override
        public void query(StringBuilder builder) {
            super.query(builder);
            builder.append("FOREIGN KEY (");
            int i = 0;
            for(String columnName : mColumnNames) {
                if(i > 0) {
                    builder.append(", ");
                }
                builder.append(columnName);
                i++;
            }
            builder.append(") ");
            mForeignKeyClause.query(builder);
        }
    }


}