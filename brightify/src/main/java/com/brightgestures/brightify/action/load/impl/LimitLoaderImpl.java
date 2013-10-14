package com.brightgestures.brightify.action.load.impl;

import com.brightgestures.brightify.action.load.BaseLoader;
import com.brightgestures.brightify.action.load.LoadQuery;
import com.brightgestures.brightify.action.load.api.GenericLoader;
import com.brightgestures.brightify.action.load.api.ListLoader;
import com.brightgestures.brightify.action.load.api.OffsetSelector;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class LimitLoaderImpl<ENTITY> extends BaseLoader<ENTITY> implements GenericLoader<ENTITY>,
        OffsetSelector<ENTITY>, ListLoader<ENTITY> {

    protected final Class<ENTITY> mType;
    protected final Integer mLimit;
    protected final Integer mOffset;

    public LimitLoaderImpl(BaseLoader<ENTITY> parentLoader, Class<ENTITY> entityClass, Integer limit) {
        this(parentLoader, entityClass, limit, null);
    }

    public LimitLoaderImpl(BaseLoader<ENTITY> parentLoader, Class<ENTITY> entityClass, Integer limit, Integer offset) {
        super(parentLoader);
        mType = entityClass;
        mLimit = limit;
        mOffset = offset;
    }

    @Override
    public void prepareQuery(LoadQuery<ENTITY> query) {
        if(mLimit != null) {
            query.setLimit(mLimit);
        }
        if (mOffset != null) {
            query.setOffset(mOffset);
        }
    }

    @Override
    public Class<ENTITY> getType() {
        return mType;
    }

    @Override
    public List<ENTITY> list() {
        return prepareResult(this).now();
    }

    @Override
    public Iterator<ENTITY> iterator() {
        return prepareResult(this).iterator();
    }

    @Override
    public <T extends ListLoader<ENTITY>> T offset(int offset) {
        return (T) new LimitLoaderImpl<ENTITY>(this, mType, mLimit, offset);
    }
}
