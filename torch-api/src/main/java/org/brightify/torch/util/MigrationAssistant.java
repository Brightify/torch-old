package org.brightify.torch.util;

import org.brightify.torch.Torch;
import org.brightify.torch.filter.Property;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface MigrationAssistant<ENTITY> {

    /**
     * Creates a property in the store.
     */
    void addProperty(Property<?> property);

// FIXME implement typesafe change type when Torch Validator comes along
//  void changePropertyType(Property<?> from, Property<?> to);

    /**
     *
     * @param from
     * @param to
     */
    @Deprecated
    void renameProperty(String from, String to);

// FIXME implement the typesafe rename when Torch Validator comes along
//  void renameProperty(Property<?> from, Property<?> to);

    /**
     * Removes a property with the given name from the store.
     */
    @Deprecated
    void removeProperty(String name);

// FIXME implement typesafe removal when Torch Validator comes along
//  void removeProperty(Property<?> property);

    void createStore();

    void deleteStore();

    void recreateStore();

    boolean storeExists();

    Torch torch();

    public interface PropertyTypeConvertor<FROM, TO> {
        TO convert(FROM from);
    }

}
