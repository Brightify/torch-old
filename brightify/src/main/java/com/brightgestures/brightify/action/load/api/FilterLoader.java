package com.brightgestures.brightify.action.load.api;

import com.brightgestures.brightify.action.load.filter.Closeable;
import com.brightgestures.brightify.action.load.filter.Filterable;
import com.brightgestures.brightify.action.load.filter.Nestable;
import com.brightgestures.brightify.action.load.filter.OperatorFilter;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface FilterLoader<ENTITY> extends ListLoader<ENTITY>, GenericLoader<ENTITY>, Filterable<ENTITY>,
        Nestable<ENTITY>, OperatorFilter<ENTITY>, Closeable<ENTITY>, OrderLoader<ENTITY>, LimitLoader<ENTITY> {
}
