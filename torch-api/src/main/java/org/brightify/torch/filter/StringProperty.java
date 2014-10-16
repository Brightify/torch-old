package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface StringProperty extends GenericProperty<String> {

    StartsWithStringFilter startsWith(String value);

    EndsWithStringFilter endsWith(String value);

    ContainsStringFilter contains(String value);

}
