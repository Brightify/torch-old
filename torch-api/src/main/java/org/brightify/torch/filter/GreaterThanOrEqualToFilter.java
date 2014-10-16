package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class GreaterThanOrEqualToFilter<TYPE extends Number> extends SingleValueFilter<TYPE, GreaterThanOrEqualToFilter<TYPE>> {
    public GreaterThanOrEqualToFilter(Property<TYPE> property, TYPE value) {
        super(property, value);
    }
}
