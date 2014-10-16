package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class NotInFilter<TYPE> extends EnumerationFilter<TYPE, NotInFilter<TYPE>> {
    public NotInFilter(Property<TYPE> property, TYPE... values) {
        super(property, values);
    }
}
