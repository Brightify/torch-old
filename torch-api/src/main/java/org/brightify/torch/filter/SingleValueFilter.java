package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class SingleValueFilter<OWNER, TYPE, FILTER extends SingleValueFilter<OWNER, TYPE, FILTER>>
        extends BaseFilter<OWNER, TYPE, FILTER> {

    private final TYPE value;

    public SingleValueFilter(Property<OWNER, TYPE> property, TYPE value) {
        super(property);
        this.value = value;
    }

    protected TYPE getValue() {
        return value;
    }
}
