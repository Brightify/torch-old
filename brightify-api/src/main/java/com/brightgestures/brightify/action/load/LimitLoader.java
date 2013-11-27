package com.brightgestures.brightify.action.load;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface LimitLoader<ENTITY> {

    OffsetListLoader<ENTITY> limit(int limit);

}
