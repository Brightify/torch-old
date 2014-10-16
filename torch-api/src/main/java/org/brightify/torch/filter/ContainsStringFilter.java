package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class ContainsStringFilter extends SingleValueFilter<String, ContainsStringFilter> {
    public ContainsStringFilter(Property<String> property, String value) {
        super(property, value);
    }
}
