package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface StringProperty<OWNER> extends GenericProperty<OWNER, String> {

    StartsWithStringFilter<OWNER> startsWith(String value);

    EndsWithStringFilter<OWNER> endsWith(String value);

    ContainsStringFilter<OWNER> contains(String value);

}
