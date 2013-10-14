package com.brightgestures.brightify.action.load.filter;

import com.brightgestures.brightify.action.load.api.ListLoader;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public interface Closeable<E> {
    /**
     * Equals to call {@link Closeable#close(int)} with value of 1
     * @return
     */
    <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> T close();

    <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> T close(int level);
}
