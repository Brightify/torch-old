package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface NumberProperty<OWNER, TYPE extends Number> extends GenericProperty<OWNER, TYPE> {

    GreaterThanFilter<OWNER, TYPE> greaterThan(TYPE value);

    LessThanFilter<OWNER, TYPE> lessThan(TYPE value);

    GreaterThanOrEqualToFilter<OWNER, TYPE> greaterThanOrEqualTo(TYPE value);

    LessThanOrEqualToFilter<OWNER, TYPE> lessThanOrEqualTo(TYPE value);

}
