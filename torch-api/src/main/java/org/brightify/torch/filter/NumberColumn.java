package org.brightify.torch.filter;

import org.brightify.torch.action.load.EntityFilter;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface NumberColumn<T> extends GenericColumn<T> {

    EntityFilter greaterThan(T value);

    EntityFilter lessThan(T value);

    EntityFilter greaterThanOrEqualTo(T value);

    EntityFilter lessThanOrEqualTo(T value);

}
