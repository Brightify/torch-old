package org.brightify.torch.action.load;

import org.brightify.torch.EntityDescription;
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
import org.brightify.torch.action.load.sync.OrderDirectionLimitListLoader;
import org.brightify.torch.action.load.sync.OrderLimitListLoader;
import org.brightify.torch.action.load.sync.OrderLoader;
import org.brightify.torch.action.load.sync.TypedFilterOrderLimitListLoader;
import org.brightify.torch.action.load.sync.TypedLoader;
import org.brightify.torch.filter.BaseFilter;
import org.brightify.torch.filter.NumberProperty;
import org.brightify.torch.filter.Property;
import org.brightify.torch.util.AsyncRunner;
import org.brightify.torch.util.Callback;
import org.brightify.torch.util.Validate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class LoaderImpl<ENTITY> implements
        Loader, TypedLoader<ENTITY>, FilterLoader<ENTITY>,
        OrderLoader<ENTITY>, DirectionLoader<ENTITY>, LimitLoader<ENTITY>, OffsetLoader<ENTITY>,
        ListLoader<ENTITY>, Countable,

        TypedFilterOrderLimitListLoader<ENTITY>,
        OrderLimitListLoader<ENTITY>, OrderDirectionLimitListLoader<ENTITY>, OffsetListLoader<ENTITY>,

        AsyncLoader, AsyncTypedLoader<ENTITY>, AsyncFilterLoader<ENTITY>,
        AsyncOrderLoader<ENTITY>, AsyncDirectionLoader<ENTITY>, AsyncLimitLoader<ENTITY>, AsyncOffsetLoader<ENTITY>,
        AsyncListLoader<ENTITY>, AsyncCountable,

        AsyncTypedFilterOrderLimitListLoader<ENTITY>,
        AsyncOrderLimitListLoader<ENTITY>, AsyncOrderDirectionLimitListLoader<ENTITY>, AsyncOffsetListLoader<ENTITY> {

    protected final Torch torch;
    protected final LoaderImpl<?> previousLoader;
    protected final LoaderType loaderType;

    public LoaderImpl(Torch torch) {
        this(torch, null, new LoaderType.NopLoaderType());
    }

    public LoaderImpl(Torch torch, LoaderImpl<?> previousLoader, LoaderType loaderType) {
        this.torch = torch;
        this.previousLoader = previousLoader;
        this.loaderType = loaderType;
    }

    public LoaderImpl<?> getPreviousLoader() {
        return previousLoader;
    }

    protected <LOCAL_ENTITY> LoaderImpl<LOCAL_ENTITY> nextLoader(LoaderType type) {
        return new LoaderImpl<LOCAL_ENTITY>(torch, this, type);
    }

    public void prepareQuery(LoaderImpl<?>.LoadQueryImpl query) {
        loaderType.prepareQuery(query);
    }

    @Override
    public AsyncLoader async() {
        return this;
    }

    @Override
    public LoaderImpl<ENTITY> desc() {
        return nextLoader(new LoaderType.DirectionLoaderType(Direction.DESCENDING));
    }

    @Override
    public List<ENTITY> list() {
        LinkedList<ENTITY> list = new LinkedList<ENTITY>();

        for (ENTITY entity : this) {
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

        return iterator.next();
    }

    @Override
    public Iterator<ENTITY> iterator() {
        return torch.getFactory().getDatabaseEngine().load(new LoadQueryImpl());
    }

    @Override
    public LoaderImpl<ENTITY> limit(int limit) {
        return nextLoader(new LoaderType.LimitLoaderType(limit));
    }

    @Override
    public LoaderImpl<ENTITY> group(Class<?> loadGroup) {
        return nextLoader(new LoaderType.SingleGroupLoaderType(loadGroup));
    }

    @Override
    public LoaderImpl<ENTITY> groups(Class<?>... loadGroups) {
        return nextLoader(new LoaderType.MultipleGroupLoaderType(loadGroups));
    }

    @Override
    public <LOCAL_ENTITY> LoaderImpl<LOCAL_ENTITY> type(Class<LOCAL_ENTITY> entityClass) {
        if (torch.getFactory().getEntities().getDescription(entityClass) == null) {
            throw new IllegalStateException("Entity not registered!");
        }
        return nextLoader(new LoaderType.TypedLoaderType(entityClass));
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
        return nextLoader(new LoaderType.OffsetLoaderType(offset));
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

        LoaderType.TypedLoaderType typedLoaderType = (LoaderType.TypedLoaderType) loaderType;
        // FIXME This should be typesafe!
        EntityDescription<ENTITY> metadata =
                torch.getFactory().getEntities().getDescription(typedLoaderType.entityClass);
        NumberProperty<Long> idColumn = metadata.getIdProperty();

        BaseFilter<?, ?> filter = null;
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
        return torch.getFactory().getDatabaseEngine().count(new LoadQueryImpl());
    }

    @Override
    public LoaderImpl<ENTITY> filter(BaseFilter<?,?> filter) {
        return nextLoader(new LoaderType.FilterLoaderType(filter));
    }

    @Override
    public LoaderImpl<ENTITY> orderBy(Property<?> column) {
        return nextLoader(new LoaderType.OrderLoaderType(column));
    }

    @Override
    public void count(Callback<Integer> callback) {
        AsyncRunner.submit(callback, new AsyncRunner.Task<Integer>() {
            @Override
            public Integer doWork() throws Exception {
                return count();
            }
        });
    }

    @Override
    public void id(Callback<ENTITY> callback, final long id) {
        AsyncRunner.submit(callback, new AsyncRunner.Task<ENTITY>() {
            @Override
            public ENTITY doWork() throws Exception {
                return id(id);
            }
        });
    }

    @Override
    public void ids(Callback<List<ENTITY>> callback, final Long... ids) {
        AsyncRunner.submit(callback, new AsyncRunner.Task<List<ENTITY>>() {
            @Override
            public List<ENTITY> doWork() throws Exception {
                return ids(ids);
            }
        });
    }

    @Override
    public void ids(Callback<List<ENTITY>> callback, final Iterable<Long> ids) {
        AsyncRunner.submit(callback, new AsyncRunner.Task<List<ENTITY>>() {
            @Override
            public List<ENTITY> doWork() throws Exception {
                return ids(ids);
            }
        });
    }

    @Override
    public void list(Callback<List<ENTITY>> callback) {
        AsyncRunner.submit(callback, new AsyncRunner.Task<List<ENTITY>>() {
            @Override
            public List<ENTITY> doWork() throws Exception {
                return list();
            }
        });
    }

    @Override
    public void single(Callback<ENTITY> callback) {
        AsyncRunner.submit(callback, new AsyncRunner.Task<ENTITY>() {
            @Override
            public ENTITY doWork() throws Exception {
                return single();
            }
        });
    }

    @Override
    public <LOCAL_ENTITY> void key(Callback<LOCAL_ENTITY> callback, final Key<LOCAL_ENTITY> key) {
        AsyncRunner.submit(callback, new AsyncRunner.Task<LOCAL_ENTITY>() {
            @Override
            public LOCAL_ENTITY doWork() throws Exception {
                return key(key);
            }
        });
    }

    @Override
    public <LOCAL_ENTITY> void keys(Callback<List<LOCAL_ENTITY>> callback, final Key<LOCAL_ENTITY>... keys) {
        AsyncRunner.submit(callback, new AsyncRunner.Task<List<LOCAL_ENTITY>>() {
            @Override
            public List<LOCAL_ENTITY> doWork() throws Exception {
                return keys(keys);
            }
        });
    }

    @Override
    public <LOCAL_ENTITY> void keys(Callback<List<LOCAL_ENTITY>> callback, final Iterable<Key<LOCAL_ENTITY>> keys) {
        AsyncRunner.submit(callback, new AsyncRunner.Task<List<LOCAL_ENTITY>>() {
            @Override
            public List<LOCAL_ENTITY> doWork() throws Exception {
                return keys(keys);
            }
        });
    }

    public class LoadQueryImpl implements LoadQuery<ENTITY> {

        private EntityDescription<ENTITY> entityDescription;
        private Set<Class<?>> loadGroups = new HashSet<Class<?>>();
        private BaseFilter<?, ?> entityFilter;
        private Map<Property<?>, OrderLoader.Direction> orderMap = new HashMap<Property<?>, Direction>();
        private Property<?> lastOrderColumn;
        private Integer limit;
        private Integer offset;

        LoadQueryImpl() {
            LinkedList<LoaderImpl<?>> loaders = new LinkedList<LoaderImpl<?>>();

            for (LoaderImpl<?> loader = LoaderImpl.this; loader != null; loader = loader.getPreviousLoader()) {
                loaders.addFirst(loader);
            }

            for (LoaderImpl<?> loader : loaders) {
                loader.prepareQuery(this);
            }
        }

        @Override
        public EntityDescription<ENTITY> getEntityDescription() {
            return entityDescription;
        }

        public void setEntityClass(Class<ENTITY> entityClass) {
            entityDescription = torch.getFactory().getEntities().getDescription(entityClass);
        }

        @Override
        public Set<Class<?>> getLoadGroups() {
            return loadGroups;
        }

        public void addLoadGroup(Class<?> group) {
            loadGroups.add(group);
        }

        public void addLoadGroups(Class<?>... groups) {
            Collections.addAll(loadGroups, groups);
        }

        @Override
        public BaseFilter<?, ?> getFilter() {
            return entityFilter;
        }

        public void setEntityFilter(BaseFilter<?, ?> entityFilter) {
            this.entityFilter = entityFilter;
        }

        @Override
        public Map<Property<?>, OrderLoader.Direction> getOrderMap() {
            return orderMap;
        }

        @Override
        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        @Override
        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }

        public void addOrdering(Property<?> orderColumn, Direction orderDirection) {
            orderMap.put(orderColumn, orderDirection);
            lastOrderColumn = orderColumn;
        }

        public void setLastOrderDirection(Direction orderDirection) {
            Validate.notNull(lastOrderColumn, "Can't change direction when there was no ordering added!");

            orderMap.put(lastOrderColumn, orderDirection);
        }




    }

}
