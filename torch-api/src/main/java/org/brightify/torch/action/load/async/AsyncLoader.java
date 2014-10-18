package org.brightify.torch.action.load.async;

import org.brightify.torch.Key;
import org.brightify.torch.util.async.Callback;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface AsyncLoader {

    AsyncLoader group(Class<?> loadGroup);

    AsyncLoader groups(Class<?>... loadGroups);

    <ENTITY> AsyncTypedFilterOrderLimitListLoader<ENTITY> type(Class<ENTITY> entityClass);

    <ENTITY> void key(Callback<ENTITY> callback, Key<ENTITY> key);

    <ENTITY> void keys(Callback<List<ENTITY>> callback, Key<ENTITY>... keys);

    <ENTITY> void keys(Callback<List<ENTITY>> callback, Iterable<Key<ENTITY>> keys);

}
