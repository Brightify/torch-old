package org.brightify.torch.sql.constraint;

import org.brightify.torch.sql.SqlQueryPart;
import org.brightify.torch.sql.clause.ConflictClause;
import org.brightify.torch.sql.clause.ForeignKeyClause;
import org.brightify.torch.sql.util.TextUtils;

public abstract class ColumnConstraint implements SqlQueryPart {

    protected String name = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract void setColumnName(String columnName);

    @Override
    public void query(StringBuilder builder) {
        if(name != null) {
            builder.append("CONSTRAINT ").append(name).append(" ");
        }
    }

    public static abstract class Check extends ColumnConstraint {
        // TODO implement "expr"

        @Override
        public void query(StringBuilder builder) {
            super.query(builder);
            throw new RuntimeException("Not implemented yet!");
        }


    }

    public static class Collate extends ColumnConstraint {

        protected String collationName = null;

        public String getCollationName() {
            return collationName;
        }

        public void setCollationName(String collationName) {
            this.collationName = collationName;
        }

        @Override
        public void setColumnName(String columnName) {
            setName(columnName + "_collate");
        }

        @Override
        public void query(StringBuilder builder) {
            super.query(builder);

            if(TextUtils.isEmpty(collationName)) {
                throw new IllegalStateException("Collation-name can't be null or empty!");
            }

            builder.append("COLLATE ").append(collationName);
        }
    }

    public abstract static class Default extends ColumnConstraint {



        @Override
        public void query(StringBuilder builder) {
            super.query(builder);
            throw new RuntimeException("Not implemented yet!");
        }
    }

    public abstract static class ForeignKey extends ColumnConstraint {

        protected ForeignKeyClause mForeignKeyClause = new ForeignKeyClause();

        public ForeignKeyClause getForeignKeyClause() {
            return mForeignKeyClause;
        }

        public void setForeignKeyClause(ForeignKeyClause foreignKeyClause) {
            mForeignKeyClause = foreignKeyClause;
        }

        @Override
        public void query(StringBuilder builder) {
            super.query(builder);

            mForeignKeyClause.query(builder);
        }
    }

    public static class NotNull extends ColumnConstraint {
        protected ConflictClause mConflictClause = new ConflictClause();

        public ConflictClause getConflictClause() {
            return mConflictClause;
        }

        public void setConflictClause(ConflictClause conflictClause) {
            mConflictClause = conflictClause;
        }

        @Override
        public void setColumnName(String columnName) {
            setName(columnName + "_nutnull");
        }

        @Override
        public void query(StringBuilder builder) {
            super.query(builder);

            builder.append("NOT NULL ");

            mConflictClause.query(builder);
        }
    }


    public static class PrimaryKey extends ColumnConstraint {

        protected Direction mDirection = null;
        protected ConflictClause mConflictClause = new ConflictClause();
        protected boolean mAutoIncrement = false;

        public ConflictClause getConflictClause() {
            return mConflictClause;
        }

        public void setConflictClause(ConflictClause conflictClause) {
            mConflictClause = conflictClause;
        }

        public boolean isAutoIncrement() {
            return mAutoIncrement;
        }

        public void setAutoIncrement(boolean autoIncrement) {
            mAutoIncrement = autoIncrement;
        }

        @Override
        public void setColumnName(String columnName) {
            setName(columnName + "_primary");
        }

        @Override
        public void query(StringBuilder builder) {
            super.query(builder);

            builder.append("PRIMARY KEY ");
            if(mDirection != null) {
                builder.append(mDirection.name()).append(" ");
            }
            mConflictClause.query(builder);
            if(mAutoIncrement) {
                builder.append(" AUTOINCREMENT");
            }
        }

        public static enum Direction {
            ASC, DESC
        }
    }

    public static class Unique extends ColumnConstraint {
        protected ConflictClause mConflictClause = new ConflictClause();

        public ConflictClause getConflictClause() {
            return mConflictClause;
        }

        public void setConflictClause(ConflictClause conflictClause) {
            mConflictClause = conflictClause;
        }

        @Override
        public void setColumnName(String columnName) {
            setName(columnName + "_unique");
        }

        @Override
        public void query(StringBuilder builder) {
            super.query(builder);

            builder.append("UNIQUE ");

            mConflictClause.query(builder);
        }
    }

}