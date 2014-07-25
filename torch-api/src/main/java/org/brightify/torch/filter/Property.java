package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Property<T> {

    String getName();

    String getSafeName();

    Class<T> getType();

    Feature[] getFeatures();

    EntityFilter equalTo(T value);

    EntityFilter notEqualTo(T value);

    public interface Feature { }

}
