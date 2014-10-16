package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class SingleValueFilter<TYPE, FILTER extends SingleValueFilter<TYPE, FILTER>>
        extends BaseFilter<TYPE, FILTER> {

    private final TYPE value;

    public SingleValueFilter(Property<TYPE> property, TYPE value) {
        super(property);
        this.value = value;
    }

    protected TYPE getValue() {
        return value;
    }
}
