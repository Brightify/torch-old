package org.brightify.torch.action.load;

import org.brightify.torch.action.load.combined.OrderLimitListLoader;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface DirectionLoader<ENTITY> {

    OrderLimitListLoader<ENTITY> desc();

}
