package org.brightify.torch.action.load.async;

import org.brightify.torch.filter.EntityFilter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface AsyncOperatorFilterLoader<ENTITY> {

    AsyncOperatorFilterOrderLimitListLoader<ENTITY> or(EntityFilter filter);

    AsyncOperatorFilterOrderLimitListLoader<ENTITY> and(EntityFilter filter);

}
