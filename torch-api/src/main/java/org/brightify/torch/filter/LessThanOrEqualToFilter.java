package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class LessThanOrEqualToFilter<TYPE extends Number> extends SingleValueFilter<TYPE, LessThanOrEqualToFilter<TYPE>> {
    public LessThanOrEqualToFilter(Property<TYPE> property, TYPE value) {
        super(property, value);
    }
}
