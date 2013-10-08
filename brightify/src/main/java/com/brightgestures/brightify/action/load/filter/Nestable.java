package com.brightgestures.brightify.action.load.filter;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public interface Nestable<E> {
    public Filterable<E> nest();
}
