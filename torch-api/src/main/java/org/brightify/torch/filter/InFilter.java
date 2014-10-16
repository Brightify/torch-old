package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class InFilter<TYPE> extends EnumerationFilter<TYPE, InFilter<TYPE>> {
    public InFilter(Property<TYPE> property, TYPE... values) {
        super(property, values);
    }
}
