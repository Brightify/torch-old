package org.brightify.torch.action.delete;

import org.brightify.torch.Key;
import org.brightify.torch.Result;

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
