package com.brightgestures.brightify.action.delete;

import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Result;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface TypedDeleter<ENTITY> {

    Result<Boolean> id(long id);
    Result<Map<Key<ENTITY>, Boolean>> ids(Long... ids);
    Result<Map<Key<ENTITY>, Boolean>> ids(Collection<Long> ids);

}
