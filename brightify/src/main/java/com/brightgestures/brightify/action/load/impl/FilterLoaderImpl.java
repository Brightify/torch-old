package com.brightgestures.brightify.action.load.impl;

import com.brightgestures.brightify.action.load.FilterLoader;
import com.brightgestures.brightify.action.load.GenericLoader;
import com.brightgestures.brightify.action.load.ListLoader;
import com.brightgestures.brightify.action.load.Loader;
import com.brightgestures.brightify.action.load.filter.Closeable;
import com.brightgestures.brightify.action.load.filter.Filterable;
import com.brightgestures.brightify.action.load.filter.Nestable;
import com.brightgestures.brightify.action.load.filter.OperatorFilter;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class FilterLoaderImpl<E> extends Loader implements FilterLoader<E> {
    public static final int DEFAULT_LEVEL = 0;

    protected final Class<E> mType;
    protected final FilterType mFilterType;
    protected final int mLevel;

    public FilterLoaderImpl(FilterLoaderImpl<E> parentLoader, FilterType filterType) {
        this(parentLoader, filterType, parentLoader.mLevel);
    }

    public FilterLoaderImpl(FilterLoaderImpl<E> parentLoader, FilterType filterType, int level) {
        this(parentLoader, parentLoader.mType, filterType, level);
    }

    public FilterLoaderImpl(Loader parentLoader, Class<E> type, FilterType filterType) {
        this(parentLoader, type, filterType, DEFAULT_LEVEL);
    }

    public FilterLoaderImpl(Loader parentLoader, Class<E> type, FilterType filterType, int level) {
        super(parentLoader);
        mType = type;
        mLevel = level;
        mFilterType = filterType;
    }

    @Override
    public Class<E> getType() {
        return mType;
    }

    @Override
    public <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> T filter(String condition, Object value) {
        return createNextLoader(
                Condition.create()
                        .setCondition(condition)
                        .setValue(value));
    }

    @Override
    public <T extends Nestable<E> & Filterable<E>> T and() {
        return createNextLoader(And.create());
    }

    @Override
    public <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> T and(String condition, Object value) {
        return and().filter(condition, value);
    }

    @Override
    public <T extends Nestable<E> & Filterable<E>> T or() {
        return createNextLoader(Or.create());
    }

    @Override
    public <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> T or(String condition, Object value) {
        return or().filter(condition, value);
    }

    @Override
    public Filterable<E> nest() {
        return createNextLoader(Open.create());
    }

    @Override
    public <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> T close() {
        return close(1);
    }

    @Override
    public <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> T close(int level) {
        if(level < 1) {
            throw new IllegalArgumentException("Level has to be >= 1!");
        }

        if(mLevel - level < 0) {
            throw new IllegalStateException("Trying to get level lower than 0!");
        }

        FilterLoaderImpl<E> loader = this;
        do {
            loader = loader.createNextLoader(Close.create());
            level--;
        } while (level > 0);

        return (T) loader;
    }

    protected <T> T createNextLoader(FilterType filterType) {
        int level = mLevel;
        if(filterType instanceof Open) {
            level += 1;
        } else if(mFilterType instanceof Close) {
            level -= 1;
        }

        return createNextLoader(filterType, level);
    }

    @SuppressWarnings("unchecked")
    protected <T> T createNextLoader(FilterType filterType, int level) {
        return (T) new FilterLoaderImpl<E>(this, filterType, level);
    }

    interface FilterType {
    }

    static class Or implements FilterType {
        public static Or create() {
            return new Or();
        }
    }

    static class And implements FilterType {
        public static And create() {
            return new And();
        }
    }

    static class Open implements FilterType {
        public static Open create() {
            return new Open();
        }
    }

    static class Close implements FilterType {
        public static Close create() {
            return new Close();
        }
    }

    static class Condition implements FilterType {
        private String mCondition;
        private Object mValue;

        public String getCondition() {
            return mCondition;
        }

        public Condition setCondition(String condition) {
            mCondition = condition;
            return this;
        }

        public Object getValue() {
            return mValue;
        }

        public Condition setValue(Object value) {
            mValue = value;
            return this;
        }

        public static Condition create() {
            return new Condition();
        }
    }
}
