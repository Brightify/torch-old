package com.brightgestures.brightify.action.load.impl;

import com.brightgestures.brightify.EntitiesCompatibility;
import com.brightgestures.brightify.Property;
import com.brightgestures.brightify.Result;
import com.brightgestures.brightify.action.load.BaseLoader;
import com.brightgestures.brightify.action.load.api.DirectionSelector;
import com.brightgestures.brightify.action.load.api.LimitLoader;
import com.brightgestures.brightify.action.load.api.ListLoader;
import com.brightgestures.brightify.action.load.LoadQuery;
import com.brightgestures.brightify.action.load.api.OffsetSelector;
import com.brightgestures.brightify.action.load.api.OrderLoader;
import com.brightgestures.brightify.action.load.api.TypedLoader;
import com.brightgestures.brightify.action.load.filter.Closeable;
import com.brightgestures.brightify.action.load.filter.Filterable;
import com.brightgestures.brightify.action.load.filter.OperatorFilter;
import com.brightgestures.brightify.util.Callback;
import com.brightgestures.brightify.util.ResultWrapper;

import java.util.Iterator;
import java.util.List;

/**
* @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
*/
public class TypedLoaderImpl<E> extends BaseLoader<E> implements TypedLoader<E> {
    protected final Class<?>[] mLoadGroups;
    protected final Class<E> mType;

    public TypedLoaderImpl(BaseLoader<E> parentLoader, Class<E> type, Class<?>... loadGroups) {
        super(parentLoader);
        mType = type;
        mLoadGroups = loadGroups;
    }


    @Override
    public Result<E> id(Long id) {
        Result<List<E>> base = ids(id);

        return new ResultWrapper<List<E>, E>(base) {
            @Override
            public void async(Callback<E> callback) {
                throw new UnsupportedOperationException("Not implemented!");
            }

            @Override
            protected E wrap(List<E> original) {
                return original.iterator().next();
            }
        };
    }

    @Override
    public void prepareQuery(LoadQuery<E> query) {
        if(query.getEntityClass() != mType) {
            throw new IllegalStateException("Type to be loaded differs! Expected: " + mType.getSimpleName() +
                    ", actual: " + query.getEntityClass().getSimpleName());
        }

        query.addLoadGroups(mLoadGroups);
    }

    @Override
    public Class<E> getType() {
        return mType;
    }

    @Override
    public <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> Result<List<E>> ids(Long... ids) {
        // create filter and add all ids
        String columnName = EntitiesCompatibility.getMetadata(mType).getIdProperty().getColumnName();
        String condition = columnName + "=";

        T loader = null;
        for(Long id : ids) {
            if(loader == null) {
                loader = filter(condition, id);
                continue;
            }
            loader = loader.or(condition, id);
        }

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public List<E> list() {
        return prepareResult(this).now();
    }

    @Override
    public Iterator<E> iterator() {
        return prepareResult(this).iterator();
    }

    @Override
    public <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> T filter(String condition, Object... values) {
        return (T) new FilterLoaderImpl<E>(this, mType, FilterLoaderImpl.Condition.create().setCondition(condition).setValues(values));
    }

    @Override
    public Filterable<E> nest() {
        return new FilterLoaderImpl<E>(this, mType, FilterLoaderImpl.Open.create(), FilterLoaderImpl.DEFAULT_LEVEL + 1);
    }

    @Override
    public TypedLoader<E> group(Class<?> loagGroup) {
        return groups(loagGroup);
    }

    @Override
    public TypedLoader<E> groups(Class<?>... loadGroups) {
        return new TypedLoaderImpl<E>(this, mType, loadGroups);
    }

    @Override
    public <T extends ListLoader<E> & OffsetSelector<E>> T limit(int limit) {
        return (T) new LimitLoaderImpl<E>(this, mType, limit);
    }

    @Override
    public <T extends ListLoader<E> & OrderLoader<E> & DirectionSelector<E> & LimitLoader<E>> T orderBy(Property orderColumn) {
        return orderBy(orderColumn.getColumnName());
    }

    @Override
    public <T extends ListLoader<E> & OrderLoader<E> & DirectionSelector<E> & LimitLoader<E>> T orderBy(String orderColumn) {
        return (T) new OrderLoaderImpl<E>(this, mType, orderColumn);
    }
}
