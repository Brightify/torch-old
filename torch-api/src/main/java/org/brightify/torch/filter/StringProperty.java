package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface StringProperty<OWNER> extends GenericProperty<OWNER, String> {

    BaseFilter<OWNER, String> startsWith(String value);

    BaseFilter<OWNER, String> endsWith(String value);

    BaseFilter<OWNER, String> contains(String value);

}
