package com.brightgestures.brightify.action.delete;

import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Result;

import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface DeleteResult<ENTITY> extends Result<Map<Key<ENTITY>, Boolean>> {
}
