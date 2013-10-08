package com.brightgestures.brightify.action.load.impl;

import com.brightgestures.brightify.action.load.ListLoader;
import com.brightgestures.brightify.action.load.Loader;
import com.brightgestures.brightify.action.load.filter.Closeable;
import com.brightgestures.brightify.action.load.filter.Filterable;
import com.brightgestures.brightify.action.load.filter.Nestable;
import com.brightgestures.brightify.action.load.filter.OperatorFilter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class LoaderImpl<E> extends Loader implements Filterable<E>, Nestable<E>, OperatorFilter<E>, Closeable<E> {
    private final List<Class<?>> mGroups = new ArrayList<Class<?>>();
    private final Class<E> mType;

    LinkedList<FilterItem> mItems = new LinkedList<FilterItem>();
    int mLevel = 0;

    public LoaderImpl(Loader parentLoader, Class<E> type) {
        super(parentLoader);
        mType = type;
    }

    @Override
    public <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> T filter(String condition, Object value) {

        addFilterItem(
                Condition.create()
                        .setCondition(condition)
                        .setValue(value));

        return (T) this;
    }

    @Override
    public <T extends Nestable<E> & Filterable<E>> T and() {
        addFilterItem(And.create());

        return (T) this;
    }


    @Override
    public <T extends Closeable<E> & OperatorFilter<E>> T and(String condition, Object value) {
        and();

        filter(condition, value);

        return (T) this;
    }

    @Override
    public <T extends Nestable<E> & Filterable<E>> T or() {
        addFilterItem(Or.create());

        return (T) this;
    }

    @Override
    public <T extends Closeable<E> & OperatorFilter<E>> T or(String condition, Object value) {
        or();

        filter(condition, value);

        return (T) this;
    }

    @Override
    public Filterable<E> nest() {
        addFilterItem(Open.create());
        mLevel++;

        return this;
    }

    @Override
    public <T extends Closeable<E> & OperatorFilter<E>> T close() {
        return close(1);
    }

    @Override
    public <T extends Closeable<E> & OperatorFilter<E>> T close(int level) {
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
