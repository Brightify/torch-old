package com.brightgestures.brightify.sql.statement;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.sql.SqlQueryPart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class Select implements SqlQueryPart {

    protected List<SelectCore> mSelectCores = new ArrayList<SelectCore>();
    protected List<CompoundOperator> mCompoundOperators = new ArrayList<CompoundOperator>();
    protected List<OrderingTerm> mOrderingTerms = new ArrayList<OrderingTerm>();

    protected String mLimitExpression = null; // TODO implement expr http://www.sqlite.org/syntaxdiagrams.html#expr
    protected String mOffsetExpression = null; // TODO implement expr http://www.sqlite.org/syntaxdiagrams.html#expr

    public Select(SelectCore initialSelectCore) {
        mSelectCores.add(initialSelectCore);
    }

    public List<SelectCore> getSelectCores() {
        return mSelectCores;
    }

    public void addSelectCore(CompoundOperator compoundOperator, SelectCore selectCore) {
        mCompoundOperators.add(compoundOperator);
        mSelectCores.add(selectCore);
    }

    public void setSelectCores(List<SelectCore> selectCores) {
        mSelectCores = selectCores;
    }

    public List<CompoundOperator> getCompoundOperators() {
        return mCompoundOperators;
    }

    public void setCompoundOperators(List<CompoundOperator> compoundOperators) {
        mCompoundOperators = compoundOperators;
    }

    public List<OrderingTerm> getOrderingTerms() {
        return mOrderingTerms;
    }

    public void addOrderingTerm(OrderingTerm orderingTerm) {
        mOrderingTerms.add(orderingTerm);
    }

    public void setOrderingTerms(List<OrderingTerm> orderingTerms) {
        mOrderingTerms = orderingTerms;
    }

    public String getLimitExpression() {
        return mLimitExpression;
    }

    public void setLimitExpression(String limitExpression) {
        mLimitExpression = limitExpression;
    }

    public String getOffsetExpression() {
        return mOffsetExpression;
    }

    public void setOffsetExpression(String offsetExpression) {
        mOffsetExpression = offsetExpression;
    }

    public String toSQLString() {
        StringBuilder builder = new StringBuilder();

        query(builder);

        return builder.toString();
    }

    public Cursor run(SQLiteDatabase db) {
        String sql = toSQLString();

        return db.rawQuery(sql, null);
    }

    @Override
    public void query(StringBuilder builder) {
        if(mSelectCores.size() == 0) {
            throw new IllegalStateException("No selections, cannot proceed!");
        }
        if(mSelectCores.size() - 1 != mCompoundOperators.size()) {
            throw new IllegalStateException("More selections than compound operators!");
        }

        Iterator<SelectCore> it1 = mSelectCores.iterator();
        Iterator<CompoundOperator> it2 = mCompoundOperators.iterator();
        int i = 0;
        while(it1.hasNext()) {
            if(i > 0) {
                builder.append(" ");
            }
            it1.next().query(builder);

            if(it2.hasNext()) {
                builder.append(" ");
                it2.next().query(builder);
            }
            i++;
        }

        if(mOrderingTerms.size() > 0) {
            builder.append("ORDER BY ");
            i = 0;
            for(OrderingTerm orderingTerm : mOrderingTerms) {
                if(i > 0) {
                    builder.append(", ");
                }
                orderingTerm.query(builder);
                i++;
            }
        }

        if(mLimitExpression != null) {
            builder.append("LIMIT ").append(mLimitExpression);

            if(mOffsetExpression != null) {
                builder.append(" OFFSET ").append(mOffsetExpression);
            }
        }
    }

    public static class SelectCore implements SqlQueryPart {
        protected Type mType = null;
        protected List<ResultColumn> mResultColumns = new ArrayList<ResultColumn>();
        protected String mFrom; // TODO implement join-source http://www.sqlite.org/syntaxdiagrams.html#join-source
        protected String mWhereExpression; // TODO implement expr http://www.sqlite.org/syntaxdiagrams.html#expr
        protected List<String> mGroupByExpressions = new ArrayList<String>(); // TODO implement expr http://www.sqlite.org/syntaxdiagrams.html#expr
        protected String mHavingExpression; // TODO implement expr http://www.sqlite.org/syntaxdiagrams.html#expr

        public Type getType() {
            return mType;
        }

        public void setType(Type type) {
            mType = type;
        }

        public List<ResultColumn> getResultColumns() {
            return mResultColumns;
        }

        public void addResultColumn(ResultColumn resultColumn) {
            mResultColumns.add(resultColumn);
        }

        public void setResultColumns(List<ResultColumn> resultColumns) {
            mResultColumns = resultColumns;
        }

        public String getFrom() {
            return mFrom;
        }

        public void setFrom(String from) {
            mFrom = from;
        }

        public String getWhereExpression() {
            return mWhereExpression;
        }

        public void setWhereExpression(String whereExpression) {
            mWhereExpression = whereExpression;
        }

        public List<String> getGroupByExpressions() {
            return mGroupByExpressions;
        }

        public void addGroupByExpression(String groupByExpression) {
            mGroupByExpressions.add(groupByExpression);
        }

        public void setGroupByExpressions(List<String> groupByExpressions) {
            mGroupByExpressions = groupByExpressions;
        }

        public String getHavingExpression() {
            return mHavingExpression;
        }

        public void setHavingExpression(String havingExpression) {
            mHavingExpression = havingExpression;
        }

        @Override
        public void query(StringBuilder builder) {
            if(mResultColumns.size() == 0) {
                throw new IllegalStateException("No column to select!");
            }

            builder.append("SELECT ");
            if(mType != null) {
                builder.append(mType.name());
            }

            int i = 0;
            for(ResultColumn resultColumn : mResultColumns) {
                if(i > 0) {
                    builder.append(", ");
                }
                resultColumn.query(builder);
                i++;
            }
            if(mFrom != null) {
                builder.append(" FROM ").append(mFrom);
            }
            if(mWhereExpression != null) {
                builder.append(" WHERE ").append(mWhereExpression);
            }
            if(mGroupByExpressions.size() > 0) {
                builder.append(" ");
                i = 0;
                for(String groupByExpression: mGroupByExpressions) {
                    if(i > 0) {
                        builder.append(", ");
                    }
                    builder.append(groupByExpression);
                    i++;
                }

                if(mHavingExpression != null) {
                    builder.append(" HAVING ").append(mHavingExpression);
                }
            }
        }

        enum Type {
            DISTINCT, ALL
        }
    }

    public static class ResultColumn implements SqlQueryPart {
        protected String mExpression = null; // TODO implement expr http://www.sqlite.org/syntaxdiagrams.html#expr
        protected String mAlias = null;

        public String getExpression() {
            return mExpression;
        }

        public void setExpression(String expression) {
            mExpression = expression;
        }

        public String getAlias() {
            return mAlias;
        }

        public void setAlias(String alias) {
            mAlias = alias;
        }

        @Override
        public void query(StringBuilder builder) {
            if(mExpression == null) {
                throw new IllegalStateException("Expression cannot be null!");
            }

            builder.append(mExpression);
            if(mAlias != null) {
                builder.append(" AS ").append(mAlias);
            }
        }

        public static ResultColumn all() {
            ResultColumn column = new ResultColumn();
            column.mExpression = "*";
            return column;
        }

        public static ResultColumn all(String tableName) {
            ResultColumn column = new ResultColumn();
            column.mExpression = tableName + ".*";
            return column;
        }
    }

    public static class JoinOp implements SqlQueryPart {
        protected boolean mJoin = false;
        protected boolean mNatural = false;
        protected Type mType = null;

        public boolean isJoin() {
            return mJoin;
        }

        public void setJoin(boolean join) {
            mJoin = join;
        }

        public boolean isNatural() {
            return mNatural;
        }

        public void setNatural(boolean natural) {
            mNatural = natural;
        }

        public Type getType() {
            return mType;
        }

        public void setType(Type type) {
            mType = type;
        }

        @Override
        public void query(StringBuilder builder) {
            if(!mJoin) {
                builder.append(",");
                return;
            }

            if(mNatural) {
                builder.append("NATURAL ");
            }
            switch (mType) {
                case LEFT:
                case INNER:
                case CROSS:
                    builder.append(mType.name()).append(" ");
                    break;
                case LEFT_OUTER:
                    builder.append("LEFT OUTER ");
                    break;
            }
            builder.append("JOIN");
        }

        enum Type {
            LEFT, LEFT_OUTER, INNER, CROSS
        }
    }

    public static class JoinConstraint implements SqlQueryPart {
        protected String mExpression; // TODO implement expr http://www.sqlite.org/syntaxdiagrams.html#expr
        protected List<String> mColumns = new ArrayList<String>();

        public String getExpression() {
            return mExpression;
        }

        public void setExpression(String expression) {
            mExpression = expression;
        }

        public List<String> getColumns() {
            return mColumns;
        }

        public void addColumn(String column) {
            mColumns.add(column);
        }

        public void setColumns(List<String> columns) {
            mColumns = columns;
        }

        @Override
        public void query(StringBuilder builder) {
            if(mExpression != null) {
                builder.append("ON ").append(mExpression);
            } else if(mColumns.size() > 0) {
                builder.append("USING (");
                int i = 0;
                for(String column : mColumns) {
                    if(i > 0) {
                        builder.append(", ");
                    }
                    builder.append(column);
                    i++;
                }
                builder.append(")");
            }
        }
    }

    public static class OrderingTerm implements SqlQueryPart {
        protected String mExpression; // TODO implement expr http://www.sqlite.org/syntaxdiagrams.html#expr
        protected String mCollationName;
        protected Direction mDirection;

        public String getExpression() {
            return mExpression;
        }

        public void setExpression(String expression) {
            mExpression = expression;
        }

        public String getCollationName() {
            return mCollationName;
        }

        public void setCollationName(String collationName) {
            mCollationName = collationName;
        }

        public Direction getDirection() {
            return mDirection;
        }

        public void setDirection(Direction direction) {
            mDirection = direction;
        }

        @Override
        public void query(StringBuilder builder) {
            builder.append(mExpression).append(" ");
            if(mCollationName != null) {
                builder.append("COLLATE ").append(mCollationName);
            }
            if(mDirection != null) {
                builder.append(mDirection.name());
            }
        }

        enum Direction {
            ASC, DESC
        }
    }

    public static class CompoundOperator implements SqlQueryPart {
        protected Type mType = null;

        public CompoundOperator(Type type) {
            mType = type;
        }

        public Type getType() {
            return mType;
        }

        public void setType(Type type) {
            mType = type;
        }

        @Override
        public void query(StringBuilder builder) {
            if(mType == null) {
                throw new IllegalStateException("CompoundOperator type cannot be null!");
            }

            switch(mType) {
                case UNION:
                case INTERSECT:
                case EXCEPT:
                    builder.append(mType.name());
                    break;
                case UNION_ALL:
                    builder.append("UNION ALL");
                    break;
            }
        }

        enum Type {
            UNION, UNION_ALL, INTERSECT, EXCEPT
        }
    }
}
