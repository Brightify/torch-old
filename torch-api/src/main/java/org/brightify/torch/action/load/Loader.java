package org.brightify.torch.action.load;

import org.brightify.torch.Key;
import org.brightify.torch.Result;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface Loader {

    Loader group(Class<?> loadGroup);
    Loader groups(Class<?>... loadGroups);

    <ENTITY> TypedFilterOrderLimitListLoader<ENTITY> type(Class<ENTITY> entityClass);

    <ENTITY> Result<ENTITY> key(Key<ENTITY> key);
    <ENTITY> Result<List<ENTITY>> keys(Key<ENTITY>... keys);
    <ENTITY> Result<List<ENTITY>> keys(Collection<Key<ENTITY>> keys);

}
