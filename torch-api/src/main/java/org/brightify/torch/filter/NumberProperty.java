package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface NumberProperty<OWNER, TYPE extends Number> extends GenericProperty<OWNER, TYPE> {

    BaseFilter<OWNER, TYPE> greaterThan(TYPE value);

    BaseFilter<OWNER, TYPE> lessThan(TYPE value);

    BaseFilter<OWNER, TYPE> greaterThanOrEqualTo(TYPE value);

    BaseFilter<OWNER, TYPE> lessThanOrEqualTo(TYPE value);

}
