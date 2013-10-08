package com.brightgestures.brightify.action.load.filter;

import com.brightgestures.brightify.action.Loader;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public interface Closeable {
    /**
     * Equals to call {@link Closeable#close(int)} with value of 1
     * @return
     */
    <T extends Closeable & OperatorFilter> T close();

    <T extends Closeable & OperatorFilter> T close(int level);
}
