package com.brightgestures.brightify.action.load.impl;

import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Result;
import com.brightgestures.brightify.action.load.InitialLoader;
import com.brightgestures.brightify.action.load.Loader;
import com.brightgestures.brightify.action.load.TypedLoader;

/**
* @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
*/
public class InitialLoaderImpl extends Loader implements InitialLoader {
    protected final Class<?>[] mLoadGroups;

    public InitialLoaderImpl(Brightify brightify) {
        super(brightify, null);
        mLoadGroups = new Class<?>[0];
    }

    protected InitialLoaderImpl(Loader parentLoader, Class<?>... loadGroups) {
        super(parentLoader);
        mLoadGroups = loadGroups;
    }

    @Override
    public <E> Result<E> key(Key<E> key) {
        return type(key.getType()).id(key.getId());
    }

    @Override
    public <E> TypedLoader<E> type(Class<E> type) {
        return new TypedLoaderImpl<E>(this, type);
    }

    @Override
    public <E> Result<E> entity(E entity) {
        return key(Key.create(entity));
    }

    @Override
    public InitialLoader group(Class<?> group) {
        return groups(group);
    }

    @Override
    public InitialLoader groups(Class<?>... groups) {
        return new InitialLoaderImpl(this, groups);
    }
}
