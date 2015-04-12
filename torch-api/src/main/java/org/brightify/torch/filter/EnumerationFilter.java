package org.brightify.torch.filter;

import java.util.Arrays;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EnumerationFilter<OWNER, TYPE> extends BaseFilter<OWNER, TYPE> {

    private final Iterable<TYPE> values;

    public EnumerationFilter(Property<OWNER, TYPE> property, FilterType filterType, Iterable<TYPE> values) {
        super(property, filterType);
        this.values = values;
    }

    public EnumerationFilter(Property<OWNER, TYPE> property, FilterType filterType, TYPE... values) {
        super(property, filterType);
        this.values = Arrays.asList(values);
    }

    protected Iterable<TYPE> getValues() {
        return values;
    }
}
