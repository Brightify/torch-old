package org.brightify.torch.action.load;

import org.brightify.torch.filter.Column;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public abstract class LoaderType<ENTITY> {

    public abstract void prepareQuery(LoadQuery.Configuration<ENTITY> configuration);

    public static class NopLoaderType<ENTITY> extends LoaderType<ENTITY> {
        @Override
        public void prepareQuery(LoadQuery.Configuration<ENTITY> configuration) {
            // does nothing
        }
    }

    public static class SingleGroupLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final Class<?> mLoadGroup;

        public SingleGroupLoaderType(Class<?> loadGroup) {
            mLoadGroup = loadGroup;
        }

        @Override
        public void prepareQuery(LoadQuery.Configuration<ENTITY> configuration) {
            configuration.addLoadGroup(mLoadGroup);
        }
    }

    public static class MultipleGroupLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final Class<?>[] mLoadGroups;

        public MultipleGroupLoaderType(Class<?>... loadGroups) {
            mLoadGroups = loadGroups;
        }

        @Override
        public void prepareQuery(LoadQuery.Configuration<ENTITY> configuration) {
            configuration.addLoadGroups(mLoadGroups);
        }
    }

    public static class TypedLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final Class<ENTITY> mEntityClass;

        public TypedLoaderType(Class<ENTITY> entityClass) {
            mEntityClass = entityClass;
        }

        @Override
        public void prepareQuery(LoadQuery.Configuration<ENTITY> configuration) {
            configuration.setEntityClass(mEntityClass);
        }
    }

    public static class FilterLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final EntityFilter mEntityFilter;

        public FilterLoaderType(EntityFilter entityFilter) {
            mEntityFilter = entityFilter;
        }

        @Override
        public void prepareQuery(LoadQuery.Configuration<ENTITY> configuration) {
            configuration.addEntityFilter(mEntityFilter);
        }
    }

    public static class OrderLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final Column<?> mOrderColumn;

        public OrderLoaderType(Column<?> orderColumn) {
            mOrderColumn = orderColumn;
        }

        @Override
        public void prepareQuery(LoadQuery.Configuration<ENTITY> configuration) {
            configuration.addOrdering(mOrderColumn, OrderLoader.Direction.ASCENDING);
        }
    }

    public static class DirectionLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final OrderLoader.Direction mDirection;

        public DirectionLoaderType(OrderLoader.Direction direction) {
            mDirection = direction;
        }

        @Override
        public void prepareQuery(LoadQuery.Configuration<ENTITY> configuration) {
            configuration.setLastOrderDirection(mDirection);
        }
    }

    public static class LimitLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final int mLimit;

        public LimitLoaderType(int limit) {
            mLimit = limit;
        }

        @Override
        public void prepareQuery(LoadQuery.Configuration<ENTITY> configuration) {
            configuration.setLimit(mLimit);
        }
    }

    public static class OffsetLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final int mOffset;

        public OffsetLoaderType(int offset) {
            mOffset = offset;
        }

        @Override
        public void prepareQuery(LoadQuery.Configuration<ENTITY> configuration) {
            configuration.setOffset(mOffset);
        }
    }

}
