package com.brightgestures.brightify.action.load.filter;

import com.brightgestures.brightify.action.Loader;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public interface OperatorFilter {
    <T extends Nestable & Filterable> T and();

    /**
     * Equal to call "and().filter(condition, value)"
     * @param condition
     * @param value
     * @return
     */
    <T extends Closeable & OperatorFilter> T and(String condition, Object value);

    <T extends Nestable & Filterable> T or();

    /**
     * Equal to call "or().filter(condition, value)"
     * @param condition
     * @param value
     * @return
     */
    <T extends Closeable & OperatorFilter> T or(String condition, Object value);
}
