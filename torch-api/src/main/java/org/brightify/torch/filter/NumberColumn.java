package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface NumberColumn<T> extends GenericColumn<T> {

    EntityFilter greaterThan(T value);

    EntityFilter lessThan(T value);

    EntityFilter greaterThanOrEqualTo(T value);

    EntityFilter lessThanOrEqualTo(T value);

}
