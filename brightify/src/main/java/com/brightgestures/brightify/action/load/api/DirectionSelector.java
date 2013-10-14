package com.brightgestures.brightify.action.load.api;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public interface DirectionSelector<ENTITY> {
    <T extends ListLoader<ENTITY> & OrderLoader<ENTITY> & LimitLoader<ENTITY>> T desc();
}
