package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface StringProperty extends GenericProperty<String> {

    EntityFilter startsWith(String value);

    EntityFilter endsWith(String value);

    EntityFilter contains(String value);

}
