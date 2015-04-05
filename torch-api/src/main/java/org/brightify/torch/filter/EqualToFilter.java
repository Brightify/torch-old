package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class EqualToFilter<OWNER, TYPE> extends SingleValueFilter<OWNER, TYPE, EqualToFilter<OWNER, TYPE>> {
    public EqualToFilter(Property<OWNER, TYPE> property, TYPE value) {
        super(property, value);
    }
}
