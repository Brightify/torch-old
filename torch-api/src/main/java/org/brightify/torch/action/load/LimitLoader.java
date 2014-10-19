package org.brightify.torch.action.load;

import org.brightify.torch.action.load.combined.OffsetListLoader;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface LimitLoader<ENTITY> {

    OffsetListLoader<ENTITY> limit(int limit);

}
