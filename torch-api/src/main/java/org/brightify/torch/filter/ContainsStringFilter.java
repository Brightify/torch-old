package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class ContainsStringFilter<OWNER> extends SingleValueFilter<OWNER, String, ContainsStringFilter<OWNER>> {
    public ContainsStringFilter(Property<OWNER, String> property, String value) {
        super(property, value);
    }
}
