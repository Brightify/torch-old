package com.brightgestures.brightify.action.load;

import android.database.Cursor;
import android.util.Log;
import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.Entities;
import com.brightgestures.brightify.EntityMetadata;

import java.util.Collections;
import java.util.LinkedList;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class LoadQuery<ENTITY> {
    private static final String TAG = LoadQuery.class.getSimpleName();

    protected final EntityMetadata<ENTITY> mEntityMetadata;
    protected final String mSql;
    protected final String[] mSelectionArgs;

    public LoadQuery(EntityMetadata<ENTITY> entityMetadata, String sql, String... selectionArgs) {
        mEntityMetadata = entityMetadata;
        mSql = sql;
        mSelectionArgs = selectionArgs;
    }

    public EntityMetadata<ENTITY> getEntityMetadata() {
        return mEntityMetadata;
    }

    public Cursor run(Brightify brightify) {
        if(brightify.getFactory().getConfiguration().isEnableQueryLogging()) {
            Log.d(TAG, mSql);
        }

        return brightify.getDatabase().rawQuery(mSql, mSelectionArgs);
    }

    public static class Builder {

        public static <ENTITY> LoadQuery<ENTITY> build(LoaderImpl<ENTITY> lastLoader) {
            LinkedList<LoaderImpl> loaders = new LinkedList<LoaderImpl>();

            for(LoaderImpl loader = lastLoader; loader != null; loader = loader.getPreviousLoader()) {
                loaders.addFirst(loader);
            }

            Configuration<ENTITY> configuration = new Configuration<ENTITY>();
            for(LoaderImpl loader : loaders) {
                loader.prepareQuery(configuration);
            }

            EntityMetadata<ENTITY> metadata = Entities.getMetadata(configuration.mEntityClass);

            StringBuilder builder = new StringBuilder();

            builder.append("SELECT ");

            String[] columns = metadata.getColumns();

            // TODO add some validation of filters
            int i = 0;
            for(String column : columns) {
                if(i > 0) {
                    builder.append(", ");
                }
                builder.append(column);
                i++;
            }

            LinkedList<String> selectionArgsList = new LinkedList<String>();
            builder.append(" FROM ").append(metadata.getTableName());
            if(configuration.mEntityFilters.size() > 0) {
                builder.append(" WHERE ");
                for(EntityFilter filter : configuration.mEntityFilters) {
                    builder.append(filter.getFilterType().toSQL(selectionArgsList));
                }
            }

            if(configuration.mOrdering.size() > 0) {
                builder.append(" ORDER BY ");
                for(Configuration.OrderPair orderPair : configuration.mOrdering) {
                    builder.append(orderPair.getColumnName()).append(" ")
                            .append(orderPair.getDirection() == OrderLoader.Direction.ASCENDING ? "ASC" : "DESC");
                }
            }

            if(configuration.mLimit != null) {
                builder.append(" LIMIT ").append(configuration.mLimit);
                if(configuration.mOffset != null) {
                    builder.append(" OFFSET ").append(configuration.mOffset);
                }
            }

            builder.append(";");

            String sql = builder.toString();
            String[] selectionArgs = selectionArgsList.toArray(new String[selectionArgsList.size()]);

            return new LoadQuery<ENTITY>(metadata, sql, selectionArgs);
        }
    }

    public static class Configuration<ENTITY> {
        protected Class<ENTITY> mEntityClass;
        protected LinkedList<Class<?>> mLoadGroups = new LinkedList<Class<?>>();
        protected LinkedList<EntityFilter> mEntityFilters = new LinkedList<EntityFilter>();
        protected LinkedList<OrderPair> mOrdering = new LinkedList<OrderPair>();
        protected Integer mLimit;
        protected Integer mOffset;

        public Configuration<ENTITY> setEntityClass(Class<ENTITY> entityClass) {
            mEntityClass = entityClass;
            return this;
        }

        public Configuration<ENTITY> addLoadGroup(Class<?> loadGroup) {
            mLoadGroups.addLast(loadGroup);
            return this;
        }

        public Configuration<ENTITY> addLoadGroups(Class<?>... loadGroups) {
            Collections.addAll(mLoadGroups, loadGroups);
            return this;
        }

        public Configuration<ENTITY> addEntityFilter(EntityFilter filter) {
//            mEntityFilters.addLast(filter);
            return this;
        }

        public Configuration<ENTITY> addOrdering(String orderColumn, OrderLoader.Direction direction) {
            mOrdering.addLast(new OrderPair(orderColumn, direction));
            return this;
        }

        public Configuration<ENTITY> setLastOrderDirection(OrderLoader.Direction direction) {
            if(mOrdering.size() == 0) {
                throw new IllegalStateException("Can't change direction when there was no ordering added!");
            }
            mOrdering.getLast().setDirection(direction);
            return this;
        }

        public Configuration<ENTITY> setLimit(Integer limit) {
            mLimit = limit;
            return this;
        }

        public Configuration<ENTITY> setOffset(Integer offset) {
            mOffset = offset;
            return this;
        }

        private static class OrderPair {
            protected String mColumnName;
            protected OrderLoader.Direction mDirection;

            public OrderPair(String columnName, OrderLoader.Direction direction) {
                mColumnName = columnName;
                mDirection = direction;
            }

            public String getColumnName() {
                return mColumnName;
            }

            public void setColumnName(String columnName) {
                mColumnName = columnName;
            }

            public OrderLoader.Direction getDirection() {
                return mDirection;
            }

            public void setDirection(OrderLoader.Direction direction) {
                mDirection = direction;
            }
        }
    }

}
