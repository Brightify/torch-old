package com.brightgestures.brightify.action.load.filter;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public interface OperatorFilter<E> {
    <T extends Nestable<E> & Filterable<E>> T and();

    /**
     * Equal to call "and().filter(condition, value)"
     * @param condition
     * @param value
     * @return
     */
    <T extends Closeable<E> & OperatorFilter<E>> T and(String condition, Object value);

    <T extends Nestable<E> & Filterable<E>> T or();

    /**
     * Equal to call "or().filter(condition, value)"
     * @param condition
     * @param value
     * @return
     */
    <T extends Closeable<E> & OperatorFilter<E>> T or(String condition, Object value);
}