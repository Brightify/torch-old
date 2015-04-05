package org.brightify.torch.action.load;

import org.brightify.torch.action.load.combined.OrderLimitListLoader;
import org.brightify.torch.filter.BaseFilter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface FilterLoader<ENTITY> {

    OrderLimitListLoader<ENTITY> filter(BaseFilter<ENTITY, ?, ?> filter);

}
