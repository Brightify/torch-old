package com.brightgestures.brightify.action.load;

import com.brightgestures.brightify.action.load.filter.Closeable;
import com.brightgestures.brightify.action.load.filter.Filterable;
import com.brightgestures.brightify.action.load.filter.Nestable;
import com.brightgestures.brightify.action.load.filter.OperatorFilter;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface FilterLoader<E> extends GenericLoader<E>, Filterable<E>, Nestable<E>, OperatorFilter<E>, Closeable<E> {
}
