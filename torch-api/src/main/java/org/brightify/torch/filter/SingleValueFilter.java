package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class SingleValueFilter<OWNER, TYPE> extends BaseFilter<OWNER, TYPE> {

    private final TYPE value;

    public SingleValueFilter(Property<OWNER, TYPE> property, FilterType filterType, TYPE value) {
        super(property, filterType);
        this.value = value;
    }

    protected TYPE getValue() {
        return value;
    }
}
