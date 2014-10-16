package org.brightify.torch.filter;

public final class GreaterThanFilter<TYPE extends Number> extends SingleValueFilter<TYPE, GreaterThanFilter<TYPE>> {
    public GreaterThanFilter(Property<TYPE> property, TYPE value) {
        super(property, value);
    }
}
