package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class NotEqualToFilter<OWNER, TYPE> extends SingleValueFilter<OWNER, TYPE, NotEqualToFilter<OWNER, TYPE>> {
    public NotEqualToFilter(Property<OWNER, TYPE> property, TYPE value) {
        super(property, value);
    }
}
