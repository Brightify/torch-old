package org.brightify.torch.action.load.sync;

import org.brightify.torch.filter.EntityFilter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface FilterLoader<ENTITY> {

    OperatorFilterOrderLimitListLoader<ENTITY> filter(EntityFilter filter);

}
