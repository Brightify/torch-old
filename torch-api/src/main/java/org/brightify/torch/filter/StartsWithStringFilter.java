package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class StartsWithStringFilter extends SingleValueFilter<String, StartsWithStringFilter> {
    public StartsWithStringFilter(Property<String> property, String value) {
        super(property, value);
    }
}
