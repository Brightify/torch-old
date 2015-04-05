package org.brightify.torch.filter;

import java.util.Arrays;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class EnumerationFilter<OWNER, TYPE, FILTER extends EnumerationFilter<OWNER, TYPE, FILTER>>
        extends BaseFilter<OWNER, TYPE, FILTER> {

    private final Iterable<TYPE> values;

    public EnumerationFilter(Property<OWNER, TYPE> property, Iterable<TYPE> values) {
        super(property);
        this.values = values;
    }

    public EnumerationFilter(Property<OWNER, TYPE> property, TYPE... values) {
        super(property);
        this.values = Arrays.asList(values);
    }

    protected Iterable<TYPE> getValues() {
        return values;
    }
}
