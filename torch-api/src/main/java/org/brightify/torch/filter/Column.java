package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Column<T> {

    String getName();

    EntityFilter equalTo(T value);

    EntityFilter notEqualTo(T value);

}
