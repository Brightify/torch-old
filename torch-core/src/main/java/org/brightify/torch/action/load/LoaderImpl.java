package org.brightify.torch.action.load;

import android.database.Cursor;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.Key;
import org.brightify.torch.Torch;
import org.brightify.torch.action.load.async.AsyncCountable;
import org.brightify.torch.action.load.async.AsyncDirectionLoader;
import org.brightify.torch.action.load.async.AsyncFilterLoader;
import org.brightify.torch.action.load.async.AsyncLimitLoader;
import org.brightify.torch.action.load.async.AsyncListLoader;
import org.brightify.torch.action.load.async.AsyncLoader;
import org.brightify.torch.action.load.async.AsyncOffsetListLoader;
import org.brightify.torch.action.load.async.AsyncOffsetLoader;
import org.brightify.torch.action.load.async.AsyncOperatorFilterLoader;
import org.brightify.torch.action.load.async.AsyncOperatorFilterOrderLimitListLoader;
import org.brightify.torch.action.load.async.AsyncOrderDirectionLimitListLoader;
import org.brightify.torch.action.load.async.AsyncOrderLimitListLoader;
import org.brightify.torch.action.load.async.AsyncOrderLoader;
import org.brightify.torch.action.load.async.AsyncTypedFilterOrderLimitListLoader;
import org.brightify.torch.action.load.async.AsyncTypedLoader;
import org.brightify.torch.action.load.sync.Countable;
import org.brightify.torch.action.load.sync.DirectionLoader;
import org.brightify.torch.action.load.sync.FilterLoader;
import org.brightify.torch.action.load.sync.LimitLoader;
import org.brightify.torch.action.load.sync.ListLoader;
import org.brightify.torch.action.load.sync.Loader;
import org.brightify.torch.action.load.sync.OffsetListLoader;
import org.brightify.torch.action.load.sync.OffsetLoader;
import org.brightify.torch.action.load.sync.OperatorFilterLoader;
import org.brightify.torch.action.load.sync.OperatorFilterOrderLimitListLoader;
import org.brightify.torch.action.load.sync.OrderDirectionLimitListLoader;
import org.brightify.torch.action.load.sync.OrderLimitListLoader;
import org.brightify.torch.action.load.sync.OrderLoader;
import org.brightify.torch.action.load.sync.TypedFilterOrderLimitListLoader;
import org.brightify.torch.action.load.sync.TypedLoader;
import org.brightify.torch.filter.Column;
import org.brightify.torch.filter.EntityFilter;
import org.brightify.torch.filter.NumberColumn;
import org.brightify.torch.util.AsyncRunner;
import org.brightify.torch.util.Callback;
import org.brightify.torch.util.LazyArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class LoaderImpl<ENTITY> implements
        Loader, TypedLoader<ENTITY>, FilterLoader<ENTITY>, OperatorFilterLoader<ENTITY>,
        OrderLoader<ENTITY>, DirectionLoader<ENTITY>, LimitLoader<ENTITY>, OffsetLoader<ENTITY>,
        ListLoader<ENTITY>, Countable,

        TypedFilterOrderLimitListLoader<ENTITY>, OperatorFilterOrderLimitListLoader<ENTITY>,
        OrderLimitListLoader<ENTITY>, OrderDirectionLimitListLoader<ENTITY>, OffsetListLoader<ENTITY>,

        AsyncLoader, AsyncTypedLoader<ENTITY>, AsyncFilterLoader<ENTITY>, AsyncOperatorFilterLoader<ENTITY>,
        AsyncOrderLoader<ENTITY>, AsyncDirectionLoader<ENTITY>, AsyncLimitLoader<ENTITY>, AsyncOffsetLoader<ENTITY>,
        AsyncListLoader<ENTITY>, AsyncCountable,

        AsyncTypedFilterOrderLimitListLoader<ENTITY>, AsyncOperatorFilterOrderLimitListLoader<ENTITY>,
        AsyncOrderLimitListLoader<ENTITY>, AsyncOrderDirectionLimitListLoader<ENTITY>, AsyncOffsetListLoader<ENTITY> {

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

    protected <LOCAL_ENTITY> LoaderImpl<LOCAL_ENTITY> nextLoader(LoaderType<LOCAL_ENTITY> type) {
        return new LoaderImpl<LOCAL_ENTITY>(torch, this, type);
    }

    public void prepareQuery(LoadQuery.Configuration<ENTITY> configuration) {
        loaderType.prepareQuery(configuration);
    }

    @Override
    public AsyncLoader async() {
        return this;
    }

    @Override
    public LoaderImpl<ENTITY> desc() {
        return nextLoader(new LoaderType.DirectionLoaderType<ENTITY>(Direction.DESCENDING));
    }

    @Override
    public List<ENTITY> list() {
        LinkedList<ENTITY> list = new LinkedList<ENTITY>();

        for(ENTITY entity : this) {
            list.addLast(entity);
        }

        return list;
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
        LoadQuery<ENTITY> query = LoadQuery.Builder.build(this);

        return new CursorIterator<ENTITY>(query.getEntityMetadata(), query.run(torch));
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
    public <LOCAL_ENTITY> LoaderImpl<LOCAL_ENTITY> type(Class<LOCAL_ENTITY> entityClass) {
        if (torch.getFactory().getEntities().getMetadata(entityClass) == null) {
            throw new IllegalStateException("Entity not registered!");
        }
        return nextLoader(new LoaderType.TypedLoaderType<LOCAL_ENTITY>(entityClass));
    }

    @Override
    public <LOCAL_ENTITY> LOCAL_ENTITY key(Key<LOCAL_ENTITY> key) {
        return type(key.getType()).id(key.getId());
    }

    @Override
    public <LOCAL_ENTITY> List<LOCAL_ENTITY> keys(Key<LOCAL_ENTITY>... keys) {
        return keys(Arrays.asList(keys));
    }

    @Override
    public <LOCAL_ENTITY> List<LOCAL_ENTITY> keys(Iterable<Key<LOCAL_ENTITY>> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("There has to be at least one key!");
        }

        Class<LOCAL_ENTITY> type = keys.iterator().next().getType();
        LinkedList<Long> ids = new LinkedList<Long>();
        for (Key<LOCAL_ENTITY> key : keys) {
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
    public LoaderImpl<ENTITY> or(EntityFilter filter) {
        return this.nextLoader(new LoaderType.FilterLoaderType<ENTITY>(new EntityFilter(null,
                new EntityFilter.OrFilterType())))
                .filter(filter);
    }

    @Override
    public LoaderImpl<ENTITY> and(EntityFilter filter) {
        return this.nextLoader(new LoaderType.FilterLoaderType<ENTITY>(new EntityFilter(null,
                                                                                        new EntityFilter
                                                                                                .AndFilterType()
        )))
                .filter(filter);
    }

    @Override
    public ENTITY id(long id) {
        return ids(Collections.singleton(id)).iterator().next();
    }

    @Override
    public List<ENTITY> ids(Long... ids) {
        return ids(Arrays.asList(ids));
    }

    @Override
    public List<ENTITY> ids(Iterable<Long> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("Ids cannot be null!");
        }

        LoaderType.TypedLoaderType<ENTITY> typedLoaderType = (LoaderType.TypedLoaderType<ENTITY>) loaderType;
        EntityMetadata<ENTITY> metadata = torch.getFactory().getEntities().getMetadata(typedLoaderType.mEntityClass);
        NumberColumn<Long> idColumn = metadata.getIdColumn();

        EntityFilter filter = null;
        for (Long id : ids) {
            if (filter == null) {
                filter = idColumn.equalTo(id);
            } else {
                filter = filter.or(idColumn.equalTo(id));
            }

        }

        return filter(filter).orderBy(idColumn).list();
    }

    @Override
    public int count() {
        return LoadQuery.Builder.build(this).count(torch);
    }

    @Override
    public LoaderImpl<ENTITY> filter(EntityFilter nestedFilter) {
        return nextLoader(new LoaderType.FilterLoaderType<ENTITY>(nestedFilter));
    }

    @Override
    public LoaderImpl<ENTITY> orderBy(Column<?> column) {
        return nextLoader(new LoaderType.OrderLoaderType<ENTITY>(column));
    }

    @Override
    public void count(Callback<Integer> callback) {
        AsyncRunner.run(callback, new AsyncRunner.Task<Integer>() {
            @Override
            public Integer doWork() throws Exception {
                return count();
            }
        });
    }

    @Override
    public void id(Callback<ENTITY> callback, final long id) {
        AsyncRunner.run(callback, new AsyncRunner.Task<ENTITY>() {
            @Override
            public ENTITY doWork() throws Exception {
                return id(id);
            }
        });
    }

    @Override
    public void ids(Callback<List<ENTITY>> callback, final Long... ids) {
        AsyncRunner.run(callback, new AsyncRunner.Task<List<ENTITY>>() {
            @Override
            public List<ENTITY> doWork() throws Exception {
                return ids(ids);
            }
        });
    }

    @Override
    public void ids(Callback<List<ENTITY>> callback, final Iterable<Long> ids) {
        AsyncRunner.run(callback, new AsyncRunner.Task<List<ENTITY>>() {
            @Override
            public List<ENTITY> doWork() throws Exception {
                return ids(ids);
            }
        });
    }

    @Override
    public void list(Callback<List<ENTITY>> callback) {
        AsyncRunner.run(callback, new AsyncRunner.Task<List<ENTITY>>() {
            @Override
            public List<ENTITY> doWork() throws Exception {
                return list();
            }
        });
    }

    @Override
    public void single(Callback<ENTITY> callback) {
        AsyncRunner.run(callback, new AsyncRunner.Task<ENTITY>() {
            @Override
            public ENTITY doWork() throws Exception {
                return single();
            }
        });
    }

    @Override
    public <LOCAL_ENTITY> void key(Callback<LOCAL_ENTITY> callback, final Key<LOCAL_ENTITY> key) {
        AsyncRunner.run(callback, new AsyncRunner.Task<LOCAL_ENTITY>() {
            @Override
            public LOCAL_ENTITY doWork() throws Exception {
                return key(key);
            }
        });
    }

    @Override
    public <LOCAL_ENTITY> void keys(Callback<List<LOCAL_ENTITY>> callback, final Key<LOCAL_ENTITY>... keys) {
        AsyncRunner.run(callback, new AsyncRunner.Task<List<LOCAL_ENTITY>>() {
            @Override
            public List<LOCAL_ENTITY> doWork() throws Exception {
                return keys(keys);
            }
        });
    }

    @Override
    public <LOCAL_ENTITY> void keys(Callback<List<LOCAL_ENTITY>> callback, final Iterable<Key<LOCAL_ENTITY>> keys) {
        AsyncRunner.run(callback, new AsyncRunner.Task<List<LOCAL_ENTITY>>() {
            @Override
            public List<LOCAL_ENTITY> doWork() throws Exception {
                return keys(keys);
            }
        });
    }
}
