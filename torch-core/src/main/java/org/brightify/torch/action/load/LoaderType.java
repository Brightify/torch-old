package org.brightify.torch.action.load;

import org.brightify.torch.filter.BaseFilter;
import org.brightify.torch.filter.Property;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
abstract class LoaderType<ENTITY> {

    public abstract void prepareQuery(LoaderImpl<ENTITY>.LoadQueryImpl query);

    public static class NopLoaderType<ENTITY> extends LoaderType<ENTITY> {
        @Override
        public void prepareQuery(LoaderImpl<ENTITY>.LoadQueryImpl configuration) {
            // does nothing
        }
    }

    public static class SingleGroupLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final Class<?> mLoadGroup;

        public SingleGroupLoaderType(Class<?> loadGroup) {
            mLoadGroup = loadGroup;
        }

        @Override
        public void prepareQuery(LoaderImpl<ENTITY>.LoadQueryImpl query) {
            query.addLoadGroup(mLoadGroup);
        }
    }

    public static class MultipleGroupLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final Class<?>[] mLoadGroups;

        public MultipleGroupLoaderType(Class<?>... loadGroups) {
            mLoadGroups = loadGroups;
        }

        @Override
        public void prepareQuery(LoaderImpl<ENTITY>.LoadQueryImpl query) {
            query.addLoadGroups(mLoadGroups);
        }
    }

    public static class TypedLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final Class<ENTITY> entityClass;

        public TypedLoaderType(Class<ENTITY> entityClass) {
            this.entityClass = entityClass;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void prepareQuery(LoaderImpl<ENTITY>.LoadQueryImpl query) {
            query.setEntityClass(entityClass);
        }
    }

    public static class FilterLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final BaseFilter<ENTITY, ?, ?> entityFilter;

        public FilterLoaderType(BaseFilter<ENTITY, ?, ?> entityFilter) {
            this.entityFilter = entityFilter;
        }

        @Override
        public void prepareQuery(LoaderImpl<ENTITY>.LoadQueryImpl query) {
            query.setEntityFilter(entityFilter);
        }
    }

    public static class OrderLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final Property<ENTITY, ?> orderProperty;

        public OrderLoaderType(Property<ENTITY, ?> orderProperty) {
            this.orderProperty = orderProperty;
        }

        @Override
        public void prepareQuery(LoaderImpl<ENTITY>.LoadQueryImpl query) {
            query.addOrdering(orderProperty, OrderLoader.Direction.ASCENDING);
        }
    }

    public static class DirectionLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final OrderLoader.Direction mDirection;

        public DirectionLoaderType(OrderLoader.Direction direction) {
            mDirection = direction;
        }

        @Override
        public void prepareQuery(LoaderImpl<ENTITY>.LoadQueryImpl query) {
            query.setLastOrderDirection(mDirection);
        }
    }

    public static class LimitLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final int mLimit;

        public LimitLoaderType(int limit) {
            mLimit = limit;
        }

        @Override
        public void prepareQuery(LoaderImpl<ENTITY>.LoadQueryImpl query) {
            query.setLimit(mLimit);
        }
    }

    public static class OffsetLoaderType<ENTITY> extends LoaderType<ENTITY> {

        protected final int mOffset;

        public OffsetLoaderType(int offset) {
            mOffset = offset;
        }

        @Override
        public void prepareQuery(LoaderImpl<ENTITY>.LoadQueryImpl query) {
            query.setOffset(mOffset);
        }
    }

}
