package com.brightgestures.brightify.action.load.impl;

import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.Entities;
import com.brightgestures.brightify.Result;
import com.brightgestures.brightify.action.load.ListLoader;
import com.brightgestures.brightify.action.load.Loader;
import com.brightgestures.brightify.action.load.TypedLoader;
import com.brightgestures.brightify.action.load.filter.Closeable;
import com.brightgestures.brightify.action.load.filter.Filterable;
import com.brightgestures.brightify.action.load.filter.OperatorFilter;
import com.brightgestures.brightify.util.ResultWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
*/
public class TypedLoaderImpl<E> extends Loader implements TypedLoader<E>, Filterable<E> {
    private final List<Class<?>> mGroups = new ArrayList<Class<?>>();
    private final Class<E> mType;

    public TypedLoaderImpl(Loader parentLoader, Class<E> type) {
        super(parentLoader);
        mType = type;
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
    public <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> Result<List<E>> ids(Long... ids) {
        // create filter and add all ids
        String columnName = Entities.getMetadata(mType).getIdProperty().getColumnName();
        String condition = columnName + "=";

        T filter = null;
        for(Long id : ids) {
            if(filter == null) {
                filter = filter(condition, id);
                continue;
            }
            filter = filter.or(condition, id);
        }

        return null;
    }

    @Override
    public <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> T filter(String condition, Object value) {
        return (T) new LoaderImpl<E>(this, mType).filter(condition, value);
    }

    @Override
    public TypedLoader<E> group(Class<?> group) {
        return groups(group);
    }

    @Override
    public TypedLoader<E> groups(Class<?>... groups) {
        Collections.addAll(mGroups, groups);
        return new TypedLoaderImpl<E>(this, mType);
    }
}
