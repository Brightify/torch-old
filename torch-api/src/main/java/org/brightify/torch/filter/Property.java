package org.brightify.torch.filter;

import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Property<OWNER, TYPE> {

    String getName();

    String getSafeName();

    Class<OWNER> getOwnerType();

    Class<TYPE> getType();

    Set<Feature> getFeatures();

    TYPE getDefaultValue();

    BaseFilter<OWNER, TYPE> equalTo(TYPE value);

    BaseFilter<OWNER, TYPE> notEqualTo(TYPE value);

    TYPE get(OWNER entity);

    void set(OWNER entity, TYPE value);

    interface Feature { }

}
