package com.brightgestures.brightify.action.save;

import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Result;

import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface SaveResult<ENTITY> extends Result<Map<Key<ENTITY>, ENTITY>> {
}
