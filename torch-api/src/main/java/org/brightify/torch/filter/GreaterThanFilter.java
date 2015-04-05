package org.brightify.torch.filter;

public final class GreaterThanFilter<OWNER, TYPE extends Number>
        extends SingleValueFilter<OWNER, TYPE, GreaterThanFilter<OWNER, TYPE>> {
    public GreaterThanFilter(Property<OWNER, TYPE> property, TYPE value) {
        super(property, value);
    }
}
