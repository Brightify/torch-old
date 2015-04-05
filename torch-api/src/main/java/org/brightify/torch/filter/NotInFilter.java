package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class NotInFilter<OWNER, TYPE> extends EnumerationFilter<OWNER, TYPE, NotInFilter<OWNER, TYPE>> {

    public NotInFilter(Property<OWNER, TYPE> property, Iterable<TYPE> values) {
        super(property, values);
    }

    public NotInFilter(Property<OWNER, TYPE> property, TYPE... values) {
        super(property, values);
    }

}
