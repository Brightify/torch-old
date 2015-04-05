package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class StartsWithStringFilter<OWNER>
        extends SingleValueFilter<OWNER, String, StartsWithStringFilter<OWNER>> {
    public StartsWithStringFilter(Property<OWNER, String> property, String value) {
        super(property, value);
    }
}
