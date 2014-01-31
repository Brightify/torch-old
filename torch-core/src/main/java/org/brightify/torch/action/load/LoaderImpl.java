package org.brightify.torch.action.load;

import org.brightify.torch.Torch;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.Key;
import org.brightify.torch.Result;
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
        OperatorFilterLoader<ENTITY>,
        OrderLoader<ENTITY>, DirectionLoader<ENTITY>, LimitLoader<ENTITY>, OffsetLoader<ENTITY>, ListLoader<ENTITY>,

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

    protected LoadResultImpl<ENTITY> prepareResult() {
        LoadQuery<ENTITY> query = LoadQuery.Builder.build(this);

        return new LoadResultImpl<ENTITY>(torch, query);
    }

    protected <ENTITY> LoaderImpl<ENTITY> nextLoader(LoaderType<ENTITY> type) {
        return new LoaderImpl<ENTITY>(torch, this, type);
    }

    @Override
    public OrderLimitListLoader<ENTITY> desc() {
        return nextLoader(new LoaderType.DirectionLoaderType<ENTITY>(Direction.DESCENDING));
    }

    @Override
    public OperatorFilterOrderLimitListLoader<ENTITY> filter(String condition, Object... params) {
        return nextLoader(new LoaderType.FilterLoaderType<ENTITY>(EntityFilter.filter(condition, params)));
    }

    @Override
    public OperatorFilterOrderLimitListLoader<ENTITY> filter(EntityFilter nestedFilter) {
        return nextLoader(new LoaderType.FilterLoaderType<ENTITY>(nestedFilter));
    }

    @Override
    public ENTITY single() {
        Iterator<ENTITY> iterator = iterator();
        if(!iterator.hasNext()) {
            return null;
        }

        return iterator().next();
    }

    @Override
    public List<ENTITY> list() {
        return prepareResult().now();
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
    public OffsetListLoader<ENTITY> limit(int limit) {
        return nextLoader(new LoaderType.LimitLoaderType<ENTITY>(limit));
    }

    @Override
    public Loader group(Class<?> loadGroup) {
        return nextLoader(new LoaderType.SingleGroupLoaderType<ENTITY>(loadGroup));
    }

    @Override
    public Loader groups(Class<?>... loadGroups) {
        return nextLoader(new LoaderType.MultipleGroupLoaderType<ENTITY>(loadGroups));
    }

    @Override
    public <ENTITY> TypedFilterOrderLimitListLoader<ENTITY> type(Class<ENTITY> entityClass) {
        if(torch.getFactory().getEntities().getMetadata(entityClass) == null) {
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
        if(keys == null || keys.size() == 0) {
            throw new IllegalArgumentException("There has to be at least one key!");
        }

        Class<ENTITY> type = keys.iterator().next().getType();
        LinkedList<Long> ids = new LinkedList<Long>();
        for(Key<ENTITY> key : keys) {
            if(key.getType() != type) {
                throw new IllegalArgumentException("The key types doesn't match!");
            }

            ids.addLast(key.getId());
        }

        return type(type).ids(ids);
    }

    @Override
    public ListLoader<ENTITY> offset(int offset) {
        return nextLoader(new LoaderType.OffsetLoaderType<ENTITY>(offset));
    }

    @Override
    public OperatorFilterOrderLimitListLoader<ENTITY> or(String condition, Object... params) {
        return this.<ENTITY>nextLoader(new LoaderType.FilterLoaderType<ENTITY>(new EntityFilter(null, new EntityFilter.OrFilterType())))
                .filter(condition, params);
    }

    @Override
    public OperatorFilterOrderLimitListLoader<ENTITY> or(EntityFilter filter) {
        return this.<ENTITY>nextLoader(new LoaderType.FilterLoaderType<ENTITY>(new EntityFilter(null, new EntityFilter.OrFilterType())))
                .filter(filter);
    }

    @Override
    public OperatorFilterOrderLimitListLoader<ENTITY> and(String condition, Object... params) {
        return this.<ENTITY>nextLoader(new LoaderType.FilterLoaderType<ENTITY>(new EntityFilter(null, new EntityFilter.OrFilterType())))
                .filter(condition, params);
    }

    @Override
    public OperatorFilterOrderLimitListLoader<ENTITY> and(EntityFilter filter) {
        return this.<ENTITY>nextLoader(new LoaderType.FilterLoaderType<ENTITY>(new EntityFilter(null, new EntityFilter.OrFilterType())))
                .filter(filter);
    }

    @Override
    public OrderDirectionLimitListLoader<ENTITY> orderBy(String columnName) {
        return nextLoader(new LoaderType.OrderLoaderType<ENTITY>(columnName));
    }

    @Override
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
        if(ids == null || ids.size() == 0) {
            throw new IllegalArgumentException("There has to be at least one id!");
        }
        LoaderType.TypedLoaderType<ENTITY> typedLoaderType = (LoaderType.TypedLoaderType<ENTITY>) loaderType;
        EntityMetadata<ENTITY> metadata = torch.getFactory().getEntities().getMetadata(typedLoaderType.mEntityClass);
        String columnName = metadata.getIdColumnName();
        String condition = columnName + "=";

        // We cast it to the LoaderImpl because we need the "prepareResult" method.
        // We can do this because we're in control of what's returned by these calls.
        LoaderImpl<ENTITY> loader = null;
        for(Long id : ids) {
            if(loader == null) {
                loader = (LoaderImpl<ENTITY>) filter(condition, id);
                continue;
            }
            loader = (LoaderImpl<ENTITY>) loader.or(condition, id);
        }
        loader = (LoaderImpl<ENTITY>) loader.orderBy(columnName);

        return loader.prepareResult();
    }
}
