package org.brightify.torch.action.load.sync;

import org.brightify.torch.Key;
import org.brightify.torch.action.AsyncSelector;
import org.brightify.torch.action.load.async.AsyncLoader;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Loader extends AsyncSelector<AsyncLoader> {

    Loader group(Class<?> loadGroup);
    Loader groups(Class<?>... loadGroups);

    <ENTITY> TypedFilterOrderLimitListLoader<ENTITY> type(Class<ENTITY> entityClass);

    <ENTITY> ENTITY key(Key<ENTITY> key);
    <ENTITY> List<ENTITY> keys(Key<ENTITY>... keys);
    <ENTITY> List<ENTITY> keys(Iterable<Key<ENTITY>> keys);

}
