package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class LessThanOrEqualToFilter<OWNER, TYPE extends Number>
        extends SingleValueFilter<OWNER, TYPE, LessThanOrEqualToFilter<OWNER, TYPE>> {
    public LessThanOrEqualToFilter(Property<OWNER, TYPE> property, TYPE value) {
        super(property, value);
    }
}
