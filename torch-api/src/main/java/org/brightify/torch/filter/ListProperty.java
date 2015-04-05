package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ListProperty<OWNER, TYPE> extends Property<OWNER, TYPE> {

    EntityFilter contains(TYPE... values);

    EntityFilter excludes(TYPE... values);

}
