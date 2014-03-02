package org.brightify.torch.action.delete;

import org.brightify.torch.Key;
import org.brightify.torch.Result;
import org.brightify.torch.action.AsyncSelector;

import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface Deleter extends AsyncSelector<AsyncDeleter> {

    <ENTITY> Boolean entity(ENTITY entity);

    <ENTITY> Map<Key<ENTITY>, Boolean> entities(ENTITY... entities);

    <ENTITY> Map<Key<ENTITY>, Boolean> entities(Iterable<ENTITY> entities);

    <ENTITY> Boolean key(Key<ENTITY> key);

    <ENTITY> Map<Key<ENTITY>, Boolean> keys(Key<ENTITY>... keys);

    <ENTITY> Map<Key<ENTITY>, Boolean> keys(Iterable<Key<ENTITY>> keys);

}
