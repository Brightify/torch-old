package com.brightgestures.brightify.action.load;

import com.brightgestures.brightify.action.Loader;
import com.brightgestures.brightify.action.load.filter.Closeable;
import com.brightgestures.brightify.action.load.filter.Filterable;
import com.brightgestures.brightify.action.load.filter.Nestable;
import com.brightgestures.brightify.action.load.filter.OperatorFilter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class FilterLoaderImpl<E> implements Filterable<E>, Nestable, OperatorFilter, Closeable {
    private final Class<E> mType;
    private final List<Class<?>> mGroups;

    LinkedList<FilterItem> mItems = new LinkedList<FilterItem>();
    int mLevel = 0;

    public FilterLoaderImpl(Class<E> type, List<Class<?>> groups) {
        mType = type;
        mGroups = groups;
    }

    @Override
    public <T extends Loader.ListLoader<E> & Closeable & OperatorFilter> T filter(String condition, Object value) {

        addFilterItem(
                Condition.create()
                        .setCondition(condition)
                        .setValue(value));

        return (T) this;
    }

    @Override
    public <T extends Nestable & Filterable> T and() {
        addFilterItem(And.create());

        return (T) this;
    }


    @Override
    public <T extends Closeable & OperatorFilter> T and(String condition, Object value) {
        and();

        filter(condition, value);

        return (T) this;
    }

    @Override
    public <T extends Nestable & Filterable> T or() {
        addFilterItem(Or.create());

        return (T) this;
    }

    @Override
    public <T extends Closeable & OperatorFilter> T or(String condition, Object value) {
        or();

        filter(condition, value);

        return (T) this;
    }

    @Override
    public Filterable nest() {
        addFilterItem(Open.create());
        mLevel++;

        return this;
    }

    @Override
    public <T extends Closeable & OperatorFilter> T close() {
        return close(1);
    }

    @Override
    public <T extends Closeable & OperatorFilter> T close(int level) {
        if(level < 1) {
            throw new IllegalArgumentException("Level has to be >= 1!");
        }

        if(mLevel - level < 0) {
            throw new IllegalStateException("Trying to get level lower than 0!");
        }

        do {
            level--;
            mLevel--;


            addFilterItem(Close.create());
        } while(level > 0);

        return (T) this;
    }

    private void addFilterItem(FilterItem item) {
        mItems.addLast(item);
    }

    interface FilterItem {
    }

    static class Or implements FilterItem {
        public static Or create() {
            return new Or();
        }
    }

    static class And implements FilterItem {
        public static And create() {
            return new And();
        }
    }

    static class Open implements FilterItem {
        public static Open create() {
            return new Open();
        }
    }

    static class Close implements FilterItem {
        public static Close create() {
            return new Close();
        }
    }

    static class Condition implements FilterItem {
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
