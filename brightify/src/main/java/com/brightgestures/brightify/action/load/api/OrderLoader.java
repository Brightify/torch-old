package com.brightgestures.brightify.action.load.api;

import com.brightgestures.brightify.Property;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public interface OrderLoader<ENTITY> {

    <T extends ListLoader<ENTITY> & OrderLoader<ENTITY> & DirectionSelector<ENTITY> & LimitLoader<ENTITY>> T orderBy(Property orderColumn);

    <T extends ListLoader<ENTITY> & OrderLoader<ENTITY> & DirectionSelector<ENTITY> & LimitLoader<ENTITY>> T orderBy(String orderColumn);

    enum Direction {
        ASCENDING, DESCENDING
    }


}
