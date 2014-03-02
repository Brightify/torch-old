package org.brightify.torch.action.save;

import org.brightify.torch.Key;
import org.brightify.torch.util.Callback;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface AsyncSaver {

    <ENTITY> void entity(Callback<Key<ENTITY>> callback, ENTITY entity);

    <ENTITY> void entities(Callback<Map<Key<ENTITY>, ENTITY>> callback, ENTITY... entities);

    <ENTITY> void entities(Callback<Map<Key<ENTITY>, ENTITY>> callback, Iterable<ENTITY> entities);

}
