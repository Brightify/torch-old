package com.brightgestures.brightify.action.load.impl;

import com.brightgestures.brightify.Property;
import com.brightgestures.brightify.action.load.BaseLoader;
import com.brightgestures.brightify.action.load.api.DirectionSelector;
import com.brightgestures.brightify.action.load.LoadQuery;
import com.brightgestures.brightify.action.load.api.GenericLoader;
import com.brightgestures.brightify.action.load.api.LimitLoader;
import com.brightgestures.brightify.action.load.api.ListLoader;
import com.brightgestures.brightify.action.load.api.OffsetSelector;
import com.brightgestures.brightify.action.load.api.OrderLoader;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class OrderLoaderImpl<ENTITY> extends BaseLoader<ENTITY> implements GenericLoader<ENTITY>, ListLoader<ENTITY>,
        OrderLoader<ENTITY>, DirectionSelector<ENTITY>, LimitLoader<ENTITY> {
    protected final Class<ENTITY> mType;
    protected final String mOrderColumn;
    protected final Direction mDirection;

    public OrderLoaderImpl(BaseLoader<ENTITY> parentLoader, Class<ENTITY> type, String orderColumn) {
        this(parentLoader, type, orderColumn, Direction.ASCENDING);
    }

    public OrderLoaderImpl(BaseLoader<ENTITY> parentLoader, Class<ENTITY> type, String orderColumn, Direction direction) {
        super(parentLoader);
        mType = type;
        mOrderColumn = orderColumn;
        mDirection = direction;
    }

    @Override
    public void prepareQuery(LoadQuery<ENTITY> query) {
        query.addOrdering(mOrderColumn, mDirection);
    }

    @Override
    public <T extends ListLoader<ENTITY> & OrderLoader<ENTITY> & LimitLoader<ENTITY>> T desc() {
        return createNextLoader(mOrderColumn, Direction.DESCENDING);
    }

    @Override
    public <T extends ListLoader<ENTITY> & OrderLoader<ENTITY> & DirectionSelector<ENTITY> & LimitLoader<ENTITY>> T orderBy(Property orderColumn) {
        return createNextLoader(orderColumn.getColumnName(), Direction.ASCENDING);
    }

    @Override
    public <T extends ListLoader<ENTITY> & OrderLoader<ENTITY> & DirectionSelector<ENTITY> & LimitLoader<ENTITY>> T orderBy(String orderColumn) {
        return createNextLoader(orderColumn, Direction.ASCENDING);
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
    public Class<ENTITY> getType() {
        return mType;
    }

    @Override
    public <T extends ListLoader<ENTITY> & OffsetSelector<ENTITY>> T limit(int limit) {
        return (T) new LimitLoaderImpl<ENTITY>(this, mType, limit);
    }

    @SuppressWarnings("unchecked")
    protected <T> T createNextLoader(String orderColumn, Direction direction) {
        return (T) new OrderLoaderImpl<ENTITY>(this, mType, orderColumn, direction);
    }
}
