package com.brightgestures.brightify.action.load.impl;

import android.database.Cursor;
import com.brightgestures.brightify.EntityMetadata;
import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Property;
import com.brightgestures.brightify.Ref;
import com.brightgestures.brightify.action.load.BaseLoader;
import com.brightgestures.brightify.action.load.CursorIterator;
import com.brightgestures.brightify.action.load.FilterLoader;
import com.brightgestures.brightify.action.load.ListLoader;
import com.brightgestures.brightify.action.load.LoadQuery;
import com.brightgestures.brightify.action.load.filter.Closeable;
import com.brightgestures.brightify.action.load.filter.Filterable;
import com.brightgestures.brightify.action.load.filter.Nestable;
import com.brightgestures.brightify.action.load.filter.OperatorFilter;
import com.brightgestures.brightify.util.Serializer;
import com.brightgestures.brightify.util.TypeUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class FilterLoaderImpl<E> extends BaseLoader<E> implements FilterLoader<E> {
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

    public FilterLoaderImpl(BaseLoader<E> parentLoader, Class<E> type, FilterType filterType) {
        this(parentLoader, type, filterType, DEFAULT_LEVEL);
    }

    public FilterLoaderImpl(BaseLoader<E> parentLoader, Class<E> type, FilterType filterType, int level) {
        super(parentLoader);
        mType = type;
        mLevel = level;
        mFilterType = filterType;
    }

    @Override
    public void prepareQuery(LoadQuery<E> query) {
        if(query.getEntityClass() != mType) {
            throw new IllegalStateException("Type to be loaded differs! Expected: " + mType.getSimpleName() +
                    ", actual: " + query.getEntityClass().getSimpleName());
        }

        query.addFilterType(mFilterType);
    }

    @Override
    public Class<E> getType() {
        return mType;
    }

    @Override
    public List<E> list() {
        ArrayList<E> list = new ArrayList<E>();

        for (E e : (Iterable<E>) this) {
            list.add(e);
        }

        return list;
    }

    @Override
    public Iterator<E> iterator() {
        LoadQuery<E> query = LoadQuery.Builder.build(this);

        Cursor cursor = query.run();

        return new CursorIterator<E>(query.getEntityMetadata(), cursor);
    }

    @Override
    public <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> T filter(String condition, Object... values) {
        return createNextLoader(
                Condition.create()
                        .setCondition(condition)
                        .setValues(values));
    }

    @Override
    public <T extends Nestable<E> & Filterable<E>> T and() {
        return createNextLoader(And.create());
    }

    @Override
    public <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> T and(String condition, Object... values) {
        return and().filter(condition, values);
    }

    @Override
    public <T extends Nestable<E> & Filterable<E>> T or() {
        return createNextLoader(Or.create());
    }

    @Override
    public <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> T or(String condition, Object... values) {
        return or().filter(condition, values);
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

    public static interface FilterType {
        String toSQL(LoadQuery loadQuery);
    }

    static class Or implements FilterType {
        @Override
        public String toSQL(LoadQuery loadQuery) {
            return " OR ";
        }

        public static Or create() {
            return new Or();
        }
    }

    static class And implements FilterType {
        @Override
        public String toSQL(LoadQuery loadQuery) {
            return " AND ";
        }

        public static And create() {
            return new And();
        }
    }

    static class Open implements FilterType {
        @Override
        public String toSQL(LoadQuery loadQuery) {
            return "(";
        }

        public static Open create() {
            return new Open();
        }
    }

    static class Close implements FilterType {
        @Override
        public String toSQL(LoadQuery loadQuery) {
            return ")";
        }

        public static Close create() {
            return new Close();
        }
    }

    static class Condition implements FilterType {
        private String mCondition;
        private Object[] mValues;

        public String getCondition() {
            return mCondition;
        }

        public Condition setCondition(String condition) {
            mCondition = condition;
            return this;
        }

        public Object[] getValues() {
            return mValues;
        }

        public Condition setValues(Object... values) {
            mValues = values;
            return this;
        }

        @Override
        public String toSQL(LoadQuery loadQuery) {
            for(Object value : mValues) {
                loadQuery.addConditionArg(value.toString());
            }
            return mCondition;
        }

        public static Condition create() {
            return new Condition();
        }
    }
}
