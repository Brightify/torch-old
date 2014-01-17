package com.brightgestures.brightify.filter;

import com.brightgestures.brightify.action.load.EntityFilter;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface StringColumn extends Column<String> {

    EntityFilter startsWith(String value);

    EntityFilter endsWith(String value);

    EntityFilter contains(String value);

}
