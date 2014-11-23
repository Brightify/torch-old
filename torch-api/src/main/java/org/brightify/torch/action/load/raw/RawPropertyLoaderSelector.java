package org.brightify.torch.action.load.raw;

import org.brightify.torch.filter.Property;

import java.util.Map;

public interface RawPropertyLoaderSelector<ENTITY> {

    <VALUE> RawPropertyLoader<ENTITY, VALUE> property(Property<VALUE> property);

    <VALUE> RawPropertyLoader<ENTITY, VALUE> property(Class<VALUE> propertyType, String propertyName);

    RawEntityLoader<ENTITY> properties(Property<?>... properties);

    RawEntityLoader<ENTITY> properties(Map<String, Class<?>> propertyNameTypeMap);

}
