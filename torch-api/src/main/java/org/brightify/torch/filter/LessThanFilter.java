package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class LessThanFilter<OWNER, TYPE extends Number>
        extends SingleValueFilter<OWNER, TYPE, LessThanFilter<OWNER, TYPE>> {
    public LessThanFilter(Property<OWNER, TYPE> property, TYPE value) {
        super(property, value);
    }
}
