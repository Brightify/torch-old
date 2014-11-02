package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ListProperty<PARENT, T> extends Property<T> {

    EntityFilter contains(T... values);

    EntityFilter excludes(T... values);

}
