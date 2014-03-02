package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface GenericColumn<T> extends Column<T> {

    EntityFilter in(T... values);

    EntityFilter notIn(T... values);

}
