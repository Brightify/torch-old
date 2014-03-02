package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface StringColumn extends GenericColumn<String> {

    EntityFilter startsWith(String value);

    EntityFilter endsWith(String value);

    EntityFilter contains(String value);

}
