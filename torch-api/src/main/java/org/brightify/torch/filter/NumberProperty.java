package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface NumberProperty<TYPE extends Number> extends GenericProperty<TYPE> {

    GreaterThanFilter<?> greaterThan(TYPE value);

    LessThanFilter<?> lessThan(TYPE value);

    GreaterThanOrEqualToFilter<?> greaterThanOrEqualTo(TYPE value);

    LessThanOrEqualToFilter<?> lessThanOrEqualTo(TYPE value);

}
