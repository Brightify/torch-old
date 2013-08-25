package com.brightgestures.droidorm.util;

import java.util.HashMap;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class ClassHashMap<T> extends HashMap<Class<? extends T>, T> {
    @SuppressWarnings("unchecked")
    public final <C extends T> C getObject(Class<C> cls) {
        return (C) super.get(cls);
    }

    @SuppressWarnings("unchecked")
    public final T putObject(T value) {
        return super.put((Class<? extends T>) value.getClass(), value);
    }
}
