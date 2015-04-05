package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class InFilter<OWNER, TYPE> extends EnumerationFilter<OWNER, TYPE, InFilter<OWNER, TYPE>> {

    public InFilter(Property<OWNER, TYPE> property, Iterable<TYPE> values) {
        super(property, values);
    }

    public InFilter(Property<OWNER, TYPE> property, TYPE... values) {
        super(property, values);
    }

}
