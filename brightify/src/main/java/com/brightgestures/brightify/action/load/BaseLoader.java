package com.brightgestures.brightify.action.load;

import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.action.load.api.ChildLoader;
import com.brightgestures.brightify.action.load.api.GenericLoader;
import com.brightgestures.brightify.action.load.api.Loader;
import com.brightgestures.brightify.action.load.impl.LoadResultImpl;

public abstract class BaseLoader<E> implements Loader, ChildLoader {
    protected final Brightify mBrightify;
    protected final BaseLoader<E> mParentLoader;

    protected BaseLoader(BaseLoader<E> parentLoader) {
        this(parentLoader.mBrightify, parentLoader);
    }

    protected BaseLoader(Brightify brightify, BaseLoader<E> parentLoader) {
        mBrightify = brightify;
        mParentLoader = parentLoader;
    }

    @Override
    public BaseLoader<E> getParentLoader() {
        return mParentLoader;
    }

    protected <T extends BaseLoader<E> & GenericLoader<E>> LoadResultImpl<E> prepareResult(T lastLoader) {
        LoadQuery<E> query = LoadQuery.Builder.build(lastLoader);

        return new LoadResultImpl<E>(mBrightify, query);
    }

    public abstract void prepareQuery(LoadQuery<E> query);


    // TODO implement! https://code.google.com/p/objectify-appengine/source/browse/src/main/java/com/googlecode/objectify/impl/LoadTypeImpl.java

}
