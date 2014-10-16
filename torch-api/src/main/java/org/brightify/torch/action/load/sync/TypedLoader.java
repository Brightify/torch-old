package org.brightify.torch.action.load.sync;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface TypedLoader<ENTITY> {

    ENTITY id(long id);

    List<ENTITY> ids(Long... ids);

    List<ENTITY> ids(Iterable<Long> ids);

}
