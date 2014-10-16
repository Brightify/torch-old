package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class LessThanFilter<TYPE extends Number> extends SingleValueFilter<TYPE, LessThanFilter<TYPE>> {
    public LessThanFilter(Property<TYPE> property, TYPE value) {
        super(property, value);
    }
}
