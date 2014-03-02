package org.brightify.torch.action.load.async;

import org.brightify.torch.filter.EntityFilter;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface AsyncFilterLoader<ENTITY> {

    AsyncOperatorFilterOrderLimitListLoader<ENTITY> filter(EntityFilter filter);

}
