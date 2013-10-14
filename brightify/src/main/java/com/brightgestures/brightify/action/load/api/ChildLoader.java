package com.brightgestures.brightify.action.load.api;

import com.brightgestures.brightify.action.load.BaseLoader;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface ChildLoader {
    public BaseLoader getParentLoader();
}
