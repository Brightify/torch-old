package org.brightify.torch.action.load;

import org.brightify.torch.EntityMetadata;
import org.brightify.torch.Key;
import org.brightify.torch.Result;
import org.brightify.torch.Torch;
import org.brightify.torch.util.Callback;
import org.brightify.torch.util.ResultWrapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class LoaderImpl<ENTITY> implements Loader, TypedLoader<ENTITY>, FilterLoader<ENTITY>,
        OperatorFilterLoader<ENTITY>, OrderLoader<ENTITY>, DirectionLoader<ENTITY>, LimitLoader<ENTITY>,
        OffsetLoader<ENTITY>, ListLoader<ENTITY>, Countable,

        TypedFilterOrderLimitListLoader<ENTITY>, OperatorFilterOrderLimitListLoader<ENTITY>,
        OrderLimitListLoader<ENTITY>,
        OrderDirectionLimitListLoader<ENTITY>, OffsetListLoader<ENTITY> {

    protected final Torch torch;
    protected final LoaderImpl<?> previousLoader;
    protected final LoaderType<ENTITY> loaderType;

    public LoaderImpl(Torch torch) {
        this(torch, null, new LoaderType.NopLoaderType<ENTITY>());
    }

    public LoaderImpl(Torch torch, LoaderImpl<?> previousLoader, LoaderType<ENTITY> loaderType) {
        this.torch = torch;
        this.previousLoader = previousLoader;
        this.loaderType = loaderType;
    }

    public LoaderImpl<?> getPreviousLoader() {
        return previousLoader;
    }

    public void prepareQuery(LoadQuery.Configuration<ENTITY> configuration) {
        loaderType.prepareQuery(configuration);
    }

    @Override
    public LoaderImpl<ENTITY> desc() {
        return nextLoader(new LoaderType.DirectionLoaderType<ENTITY>(Direction.DESCENDING));
    }

    protected <ENTITY> LoaderImpl<ENTITY> nextLoader(LoaderType<ENTITY> type) {
        return new LoaderImpl<ENTITY>(torch, this, type);
    }

    @Override
    public List<ENTITY> list() {
        return prepareResult().now();
    }

    protected LoadResultImpl<ENTITY> prepareResult() {
        LoadQuery<ENTITY> query = LoadQuery.Builder.build(this);

        return new LoadResultImpl<ENTITY>(torch, query);
    }

    @Override
    public ENTITY single() {
        Iterator<ENTITY> iterator = iterator();
        if (!iterator.hasNext()) {
            return null;
        }

        return iterator().next();
    }

    @Override
    public Iterator<ENTITY> iterator() {
        return prepareResult().iterator();
    }

    @Override
    public void async(Callback<List<ENTITY>> callback) {
        prepareResult().async(callback);
    }

    @Override
    public LoaderImpl<ENTITY> limit(int limit) {
        return nextLoader(new LoaderType.LimitLoaderType<ENTITY>(limit));
    }

    @Override
    public LoaderImpl<ENTITY> group(Class<?> loadGroup) {
        return nextLoader(new LoaderType.SingleGroupLoaderType<ENTITY>(loadGroup));
    }

    @Override
    public LoaderImpl<ENTITY> groups(Class<?>... loadGroups) {
        return nextLoader(new LoaderType.MultipleGroupLoaderType<ENTITY>(loadGroups));
    }

    @Override
    public <ENTITY> LoaderImpl<ENTITY> type(Class<ENTITY> entityClass) {
        if (torch.getFactory().getEntities().getMetadata(entityClass) == null) {
            throw new IllegalStateException("Entity not registered!");
        }
        return nextLoader(new LoaderType.TypedLoaderType<ENTITY>(entityClass));
    }

    @Override
    public <ENTITY> Result<ENTITY> key(Key<ENTITY> key) {
        return type(key.getType()).id(key.getId());
    }

    @Override
    public <ENTITY> Result<List<ENTITY>> keys(Key<ENTITY>... keys) {
        return keys(Arrays.asList(keys));
    }

    @Override
    public <ENTITY> Result<List<ENTITY>> keys(Collection<Key<ENTITY>> keys) {
        if (keys == null || keys.size() == 0) {
            throw new IllegalArgumentException("There has to be at least one key!");
        }

        Class<ENTITY> type = keys.iterator().next().getType();
        LinkedList<Long> ids = new LinkedList<Long>();
        for (Key<ENTITY> key : keys) {
            if (key.getType() != type) {
                throw new IllegalArgumentException("The key types doesn't match!");
            }

            ids.addLast(key.getId());
        }

        return type(type).ids(ids);
    }

    @Override
    public LoaderImpl<ENTITY> offset(int offset) {
        return nextLoader(new LoaderType.OffsetLoaderType<ENTITY>(offset));
    }

    @Override
    public LoaderImpl<ENTITY> or(String condition, Object... params) {
        return this.<ENTITY>nextLoader(new LoaderType.FilterLoaderType<ENTITY>(new EntityFilter(null,
                new EntityFilter.OrFilterType())))
                .filter(condition, params);
    }

    @Override
    public LoaderImpl<ENTITY> or(EntityFilter filter) {
        return this.<ENTITY>nextLoader(new LoaderType.FilterLoaderType<ENTITY>(new EntityFilter(null,
                new EntityFilter.OrFilterType())))
                .filter(filter);
    }

    @Override
    public LoaderImpl<ENTITY> and(String condition, Object... params) {
        return this.<ENTITY>nextLoader(new LoaderType.FilterLoaderType<ENTITY>(new EntityFilter(null,
                new EntityFilter.AndFilterType())))
                .filter(condition, params);
    }

    @Override
    public LoaderImpl<ENTITY> and(EntityFilter filter) {
        return this.<ENTITY>nextLoader(new LoaderType.FilterLoaderType<ENTITY>(new EntityFilter(null,
                new EntityFilter.AndFilterType())))
                .filter(filter);
    }

    public Result<ENTITY> id(long id) {
        Result<List<ENTITY>> base = ids(Collections.singleton(id));

        return new ResultWrapper<List<ENTITY>, ENTITY>(base) {
            @Override
            protected ENTITY wrap(List<ENTITY> original) {
                return original.iterator().next();
            }

            @Override
            public void async(Callback<ENTITY> callback) {
                throw new UnsupportedOperationException("Not implemented!");
            }
        };
    }

    @Override
    public Result<List<ENTITY>> ids(Long... ids) {
        return ids(Arrays.asList(ids));
    }

    @Override
    public Result<List<ENTITY>> ids(Collection<Long> ids) {
        if (ids == null || ids.size() == 0) {
            throw new IllegalArgumentException("There has to be at least one id!");
        }
        LoaderType.TypedLoaderType<ENTITY> typedLoaderType = (LoaderType.TypedLoaderType<ENTITY>) loaderType;
        EntityMetadata<ENTITY> metadata = torch.getFactory().getEntities().getMetadata(typedLoaderType.mEntityClass);
        String columnName = metadata.getIdColumn().getName();
        String condition = columnName + "=";

        EntityFilter filter = null;
        for (Long id : ids) {
            if (filter == null) {
                filter = EntityFilter.filter(condition, id);
            } else {
                filter = filter.or(condition, id);
            }

        }

        return filter(filter).orderBy(columnName).prepareResult();
    }

    @Override
    public int count() {
        return prepareResult().count();
    }

    @Override
    public LoaderImpl<ENTITY> filter(String condition, Object... params) {
        return nextLoader(new LoaderType.FilterLoaderType<ENTITY>(EntityFilter.filter(condition, params)));
    }

    @Override
    public LoaderImpl<ENTITY> filter(EntityFilter nestedFilter) {
        return nextLoader(new LoaderType.FilterLoaderType<ENTITY>(nestedFilter));
    }

    @Override
    public LoaderImpl<ENTITY> orderBy(String columnName) {
        return nextLoader(new LoaderType.OrderLoaderType<ENTITY>(columnName));
    }
}
