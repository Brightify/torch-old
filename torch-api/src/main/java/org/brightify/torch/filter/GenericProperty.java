package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface GenericProperty<T> extends Property<T> {

    EntityFilter in(T... values);

    EntityFilter notIn(T... values);

}
