package com.brightgestures.brightify.filter;

import com.brightgestures.brightify.action.load.EntityFilter;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface Column<T> {

    String getName();

    EntityFilter equalTo(T value);

    EntityFilter notEqualTo(T value);

    EntityFilter in(T... values);

    EntityFilter notIn(T... values);

}
