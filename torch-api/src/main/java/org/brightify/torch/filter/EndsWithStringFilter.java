package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class EndsWithStringFilter<OWNER> extends SingleValueFilter<OWNER, String, EndsWithStringFilter<OWNER>> {
    public EndsWithStringFilter(Property<OWNER, String> property, String value) {
        super(property, value);
    }
}
