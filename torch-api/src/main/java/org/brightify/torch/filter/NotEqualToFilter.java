package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class NotEqualToFilter<TYPE> extends SingleValueFilter<TYPE, NotEqualToFilter<TYPE>> {
    public NotEqualToFilter(Property<TYPE> property, TYPE value) {
        super(property, value);
    }
}
