package com.brightgestures.brightify.action.load.api;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public interface LimitLoader<E> {

    <T extends ListLoader<E> & OffsetSelector<E>> T limit(int limit);

}
