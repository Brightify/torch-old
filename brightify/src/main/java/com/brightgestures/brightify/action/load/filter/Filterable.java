package com.brightgestures.brightify.action.load.filter;

import com.brightgestures.brightify.action.Loader;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public interface Filterable<E> {
    <T extends Loader.ListLoader<E> & Closeable & OperatorFilter> T filter(String condition, Object value);
}
