package org.brightify.torch.filter;

import java.util.Arrays;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class EnumerationFilter<TYPE, FILTER extends EnumerationFilter<TYPE, FILTER>>
        extends BaseFilter<TYPE, FILTER> {

    private final Iterable<TYPE> values;

    public EnumerationFilter(Property<TYPE> property, Iterable<TYPE> values) {
        super(property);
        this.values = values;
    }

    public EnumerationFilter(Property<TYPE> property, TYPE... values) {
        super(property);
        this.values = Arrays.asList(values);
    }

    protected Iterable<TYPE> getValues() {
        return values;
    }
}
