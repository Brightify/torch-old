package org.brightify.torch.filter;

import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Property<TYPE> {

    String getName();

    String getSafeName();

    Class<TYPE> getType();

    Set<Feature> getFeatures();

    TYPE getDefaultValue();

    EqualToFilter<?> equalTo(TYPE value);

    NotEqualToFilter<?> notEqualTo(TYPE value);

    public interface Feature { }

}
