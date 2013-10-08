package com.brightgestures.brightify.action.load.filter;

import com.brightgestures.brightify.action.load.ListLoader;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public interface Filterable<E> {
    <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> T filter(String condition, Object value);
}
