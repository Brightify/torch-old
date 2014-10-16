package org.brightify.torch.action.load.async;

import org.brightify.torch.filter.BaseFilter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface AsyncFilterLoader<ENTITY> {

    AsyncOrderLimitListLoader<ENTITY> filter(BaseFilter<?, ?> filter);

}
