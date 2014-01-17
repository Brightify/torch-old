package com.brightgestures.brightify.filter;

import com.brightgestures.brightify.action.load.EntityFilter;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface NumberColumn<T> extends Column<T> {

    EntityFilter greaterThan(T value);

    EntityFilter lessThan(T value);

    EntityFilter greaterThanOrEqualTo(T value);

    EntityFilter lessThanOrEqualTo(T value);

}
