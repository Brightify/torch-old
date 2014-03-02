package org.brightify.torch.action.delete;

import org.brightify.torch.Key;
import org.brightify.torch.util.Callback;

import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface AsyncDeleter {

    <ENTITY> void entity(Callback<Boolean> callback, ENTITY entity);

    <ENTITY> void entities(Callback<Map<Key<ENTITY>, Boolean>> callback, ENTITY... entities);

    <ENTITY> void entities(Callback<Map<Key<ENTITY>, Boolean>> callback, Iterable<ENTITY> entities);

    <ENTITY> void key(Callback<Boolean> callback, Key<ENTITY> key);

    <ENTITY> void keys(Callback<Map<Key<ENTITY>, Boolean>> callback, Key<ENTITY>... keys);

    <ENTITY> void keys(Callback<Map<Key<ENTITY>, Boolean>> callback, Iterable<Key<ENTITY>> keys);

}
