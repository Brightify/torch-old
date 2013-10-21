package com.brightgestures.brightify.action.save;

import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Result;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface Saver {

    <ENTITY> Result<Key<ENTITY>> entity(ENTITY entity);
    <ENTITY> Result<Map<Key<ENTITY>, ENTITY>> entities(ENTITY... entities);
    <ENTITY> Result<Map<Key<ENTITY>, ENTITY>> entities(Collection<ENTITY> entities);

}
