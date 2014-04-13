package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Property<T> {

    String getName();

    EntityFilter equalTo(T value);

    EntityFilter notEqualTo(T value);

}
