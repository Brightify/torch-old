package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class EndsWithStringFilter extends SingleValueFilter<String, EndsWithStringFilter> {
    public EndsWithStringFilter(Property<String> property, String value) {
        super(property, value);
    }
}
