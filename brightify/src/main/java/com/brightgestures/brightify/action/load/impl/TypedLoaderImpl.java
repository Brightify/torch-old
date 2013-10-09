package com.brightgestures.brightify.action.load.impl;

import com.brightgestures.brightify.Entities;
import com.brightgestures.brightify.Result;
import com.brightgestures.brightify.action.load.ListLoader;
import com.brightgestures.brightify.action.load.Loader;
import com.brightgestures.brightify.action.load.TypedLoader;
import com.brightgestures.brightify.action.load.filter.Closeable;
import com.brightgestures.brightify.action.load.filter.Filterable;
import com.brightgestures.brightify.action.load.filter.OperatorFilter;
import com.brightgestures.brightify.util.ResultWrapper;

import java.util.Iterator;
import java.util.List;

/**
* @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
*/
public class TypedLoaderImpl<E> extends Loader implements TypedLoader<E> {
    protected final Class<?>[] mLoadGroups;
    protected final Class<E> mType;

    public TypedLoaderImpl(Loader parentLoader, Class<E> type, Class<?>... loadGroups) {
        super(parentLoader);
        mType = type;
        mLoadGroups = loadGroups;
    }

    @Override
    public Result<E> id(Long id) {
        Result<List<E>> base = ids(id);

        return new ResultWrapper<List<E>, E>(base) {
            @Override
            protected E wrap(List<E> original) {
                return original.iterator().next();
            }
        };
    }

    @Override
    public Class<E> getType() {
        return mType;
    }

    @Override
    public <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> Result<List<E>> ids(Long... ids) {
        // create filter and add all ids
        String columnName = Entities.getMetadata(mType).getIdProperty().getColumnName();
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
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> T filter(String condition, Object value) {
        return (T) new FilterLoaderImpl<E>(this, mType, FilterLoaderImpl.Condition.create().setCondition(condition).setValue(value));
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
}
