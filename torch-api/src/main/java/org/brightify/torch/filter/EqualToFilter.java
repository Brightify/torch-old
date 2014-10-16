package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class EqualToFilter<TYPE> extends SingleValueFilter<TYPE, EqualToFilter<TYPE>> {
    public EqualToFilter(Property<TYPE> property, TYPE value) {
        super(property, value);
    }
}
