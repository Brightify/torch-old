package com.brightgestures.brightify.action.delete;

import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Result;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface Deleter {

    <ENTITY> Result<Boolean> entity(ENTITY entity);
    <ENTITY> Result<Map<Key<ENTITY>, Boolean>> entities(ENTITY... entities);
    <ENTITY> Result<Map<Key<ENTITY>, Boolean>> entities(Collection<ENTITY> entities);

    <ENTITY> Result<Boolean> key(Key<ENTITY> key);
    <ENTITY> Result<Map<Key<ENTITY>, Boolean>> keys(Key<ENTITY>... keys);
    <ENTITY> Result<Map<Key<ENTITY>, Boolean>> keys(Collection<Key<ENTITY>> keys);

    <ENTITY> TypedDeleter<ENTITY> type(Class<ENTITY> entityClass);

}
