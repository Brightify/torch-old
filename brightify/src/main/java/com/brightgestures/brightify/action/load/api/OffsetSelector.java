package com.brightgestures.brightify.action.load.api;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface OffsetSelector<E> {

    <T extends ListLoader<E>> T offset(int offset);

}
