package org.brightify.torch.action.load;

import org.brightify.torch.Key;
import org.brightify.torch.action.load.combined.TypedFilterOrderLimitListLoader;
import org.brightify.torch.util.async.Callback;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Loader {

    Loader group(Class<?> loadGroup);

    Loader groups(Class<?>... loadGroups);

    <ENTITY> TypedFilterOrderLimitListLoader<ENTITY> type(Class<ENTITY> entityClass);

    <ENTITY> ENTITY key(Key<ENTITY> key);

    <ENTITY> List<ENTITY> keys(Key<ENTITY>... keys);

    <ENTITY> List<ENTITY> keys(Iterable<Key<ENTITY>> keys);

    <ENTITY> void key(Callback<ENTITY> callback, Key<ENTITY> key);

    <ENTITY> void keys(Callback<List<ENTITY>> callback, Key<ENTITY>... keys);

    <ENTITY> void keys(Callback<List<ENTITY>> callback, Iterable<Key<ENTITY>> keys);

}
