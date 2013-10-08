package com.brightgestures.brightify.action.load.filter;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public interface Closeable<E> {
    /**
     * Equals to call {@link Closeable#close(int)} with value of 1
     * @return
     */
    <T extends Closeable<E> & OperatorFilter<E>> T close();

    <T extends Closeable<E> & OperatorFilter<E>> T close(int level);
}
