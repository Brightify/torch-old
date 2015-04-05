package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class GreaterThanOrEqualToFilter<OWNER, TYPE extends Number>
        extends SingleValueFilter<OWNER, TYPE, GreaterThanOrEqualToFilter<OWNER, TYPE>> {
    public GreaterThanOrEqualToFilter(Property<OWNER, TYPE> property, TYPE value) {
        super(property, value);
    }
}
