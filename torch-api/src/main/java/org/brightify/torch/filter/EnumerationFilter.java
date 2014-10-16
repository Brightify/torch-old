package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class EnumerationFilter<TYPE, FILTER extends EnumerationFilter<TYPE, FILTER>>
        extends BaseFilter<TYPE, FILTER> {

    private final TYPE[] values;

    public EnumerationFilter(Property<TYPE> property, TYPE... values) {
        super(property);
        this.values = values;
    }

    protected TYPE[] getValues() {
        return values;
    }
}
