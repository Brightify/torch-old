package org.brightify.torch.action.load;

import org.brightify.torch.action.load.sync.OrderLoader;
import org.brightify.torch.filter.Property;
import org.brightify.torch.filter.EntityFilter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
abstract class LoaderType {

    public abstract void prepareQuery(LoaderImpl<?>.LoadQueryImpl query);

    public static class NopLoaderType extends LoaderType {
        @Override
        public void prepareQuery(LoaderImpl<?>.LoadQueryImpl configuration) {
            // does nothing
        }
    }

    public static class SingleGroupLoaderType extends LoaderType {

        protected final Class<?> mLoadGroup;

        public SingleGroupLoaderType(Class<?> loadGroup) {
            mLoadGroup = loadGroup;
        }

        @Override
        public void prepareQuery(LoaderImpl<?>.LoadQueryImpl query) {
            query.addLoadGroup(mLoadGroup);
        }
    }

    public static class MultipleGroupLoaderType extends LoaderType {

        protected final Class<?>[] mLoadGroups;

        public MultipleGroupLoaderType(Class<?>... loadGroups) {
            mLoadGroups = loadGroups;
        }

        @Override
        public void prepareQuery(LoaderImpl<?>.LoadQueryImpl query) {
            query.addLoadGroups(mLoadGroups);
        }
    }

    public static class TypedLoaderType extends LoaderType {

        @SuppressWarnings("rawtypes")
        protected final Class mEntityClass;

        public TypedLoaderType(Class<?> entityClass) {
            mEntityClass = entityClass;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void prepareQuery(LoaderImpl<?>.LoadQueryImpl query) {
            query.setEntityClass(mEntityClass);
        }
    }

    public static class FilterLoaderType extends LoaderType {

        protected final EntityFilter mEntityFilter;

        public FilterLoaderType(EntityFilter entityFilter) {
            mEntityFilter = entityFilter;
        }

        @Override
        public void prepareQuery(LoaderImpl<?>.LoadQueryImpl query) {
            query.addEntityFilter(mEntityFilter);
        }
    }

    public static class OrderLoaderType extends LoaderType {

        protected final Property<?> mOrderProperty;

        public OrderLoaderType(Property<?> orderProperty) {
            mOrderProperty = orderProperty;
        }

        @Override
        public void prepareQuery(LoaderImpl<?>.LoadQueryImpl query) {
            query.addOrdering(mOrderColumn, OrderLoader.Direction.ASCENDING);
        }
    }

    public static class DirectionLoaderType extends LoaderType {

        protected final OrderLoader.Direction mDirection;

        public DirectionLoaderType(OrderLoader.Direction direction) {
            mDirection = direction;
        }

        @Override
        public void prepareQuery(LoaderImpl<?>.LoadQueryImpl query) {
            query.setLastOrderDirection(mDirection);
        }
    }

    public static class LimitLoaderType extends LoaderType {

        protected final int mLimit;

        public LimitLoaderType(int limit) {
            mLimit = limit;
        }

        @Override
        public void prepareQuery(LoaderImpl<?>.LoadQueryImpl query) {
            query.setLimit(mLimit);
        }
    }

    public static class OffsetLoaderType extends LoaderType {

        protected final int mOffset;

        public OffsetLoaderType(int offset) {
            mOffset = offset;
        }

        @Override
        public void prepareQuery(LoaderImpl<?>.LoadQueryImpl query) {
            query.setOffset(mOffset);
        }
    }

}
