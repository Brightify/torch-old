package com.brightgestures.brightify.action.load;

import android.database.Cursor;
import android.util.Log;
import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.EntityMetadata;
import com.brightgestures.brightify.Settings;

import java.util.Collections;
import java.util.LinkedList;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class LoadQuery<ENTITY> {
    private static final String TAG = LoadQuery.class.getSimpleName();

    protected final EntityMetadata<ENTITY> entityMetadata;
    protected final String sql;
    protected final String[] selectionArgs;

    public LoadQuery(EntityMetadata<ENTITY> entityMetadata, String sql, String... selectionArgs) {
        this.entityMetadata = entityMetadata;
        this.sql = sql;
        this.selectionArgs = selectionArgs;
    }

    public EntityMetadata<ENTITY> getEntityMetadata() {
        return entityMetadata;
    }

    public Cursor run(Brightify brightify) {
        if(Settings.isQueryLoggingEnabled()) {
            Log.d(TAG, sql);
        }

        return brightify.getDatabase().rawQuery(sql, selectionArgs);
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

            EntityMetadata<ENTITY> metadata = lastLoader.brightify
                                                        .getFactory().getEntities().getMetadata(configuration.entityClass);

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
            if(configuration.entityFilters.size() > 0) {
                builder.append(" WHERE ");
                for(EntityFilter filter : configuration.entityFilters) {
                    builder.append(filter.getFilterType().toSQL(selectionArgsList));
                }
            }

            if(configuration.ordering.size() > 0) {
                builder.append(" ORDER BY ");
                for(Configuration.OrderPair orderPair : configuration.ordering) {
                    builder.append(orderPair.getColumnName()).append(" ")
                            .append(orderPair.getDirection() == OrderLoader.Direction.ASCENDING ? "ASC" : "DESC");
                }
            }

            if(configuration.limit != null) {
                builder.append(" LIMIT ").append(configuration.limit);
                if(configuration.offset != null) {
                    builder.append(" OFFSET ").append(configuration.offset);
                }
            }

            builder.append(";");

            String sql = builder.toString();
            String[] selectionArgs = selectionArgsList.toArray(new String[selectionArgsList.size()]);

            return new LoadQuery<>(metadata, sql, selectionArgs);
        }
    }

    public static class Configuration<ENTITY> {
        protected Class<ENTITY> entityClass;
        protected LinkedList<Class<?>> loadGroups = new LinkedList<Class<?>>();
        protected LinkedList<EntityFilter> entityFilters = new LinkedList<EntityFilter>();
        protected LinkedList<OrderPair> ordering = new LinkedList<OrderPair>();
        protected Integer limit;
        protected Integer offset;

        public Configuration<ENTITY> setEntityClass(Class<ENTITY> entityClass) {
            this.entityClass = entityClass;
            return this;
        }

        public Configuration<ENTITY> addLoadGroup(Class<?> loadGroup) {
            loadGroups.addLast(loadGroup);
            return this;
        }

        public Configuration<ENTITY> addLoadGroups(Class<?>... loadGroups) {
            Collections.addAll(this.loadGroups, loadGroups);
            return this;
        }

        public Configuration<ENTITY> addEntityFilter(EntityFilter filter) {
            entityFilters.addLast(filter);
            return this;
        }

        public Configuration<ENTITY> addOrdering(String orderColumn, OrderLoader.Direction direction) {
            ordering.addLast(new OrderPair(orderColumn, direction));
            return this;
        }

        public Configuration<ENTITY> setLastOrderDirection(OrderLoader.Direction direction) {
            if(ordering.size() == 0) {
                throw new IllegalStateException("Can't change direction when there was no ordering added!");
            }
            ordering.getLast().setDirection(direction);
            return this;
        }

        public Configuration<ENTITY> setLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Configuration<ENTITY> setOffset(Integer offset) {
            this.offset = offset;
            return this;
        }

        private static class OrderPair {
            protected String columnName;
            protected OrderLoader.Direction direction;

            public OrderPair(String columnName, OrderLoader.Direction direction) {
                this.columnName = columnName;
                this.direction = direction;
            }

            public String getColumnName() {
                return columnName;
            }

            public void setColumnName(String columnName) {
                this.columnName = columnName;
            }

            public OrderLoader.Direction getDirection() {
                return direction;
            }

            public void setDirection(OrderLoader.Direction direction) {
                this.direction = direction;
            }
        }
    }

}
