package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Property<TYPE> {

    String getName();

    String getSafeName();

    Class<TYPE> getType();

    Feature[] getFeatures();

    EqualToFilter<?> equalTo(TYPE value);

    NotEqualToFilter<?> notEqualTo(TYPE value);

    public interface Feature { }

}
