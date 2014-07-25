package org.brightify.torch.util;

import org.brightify.torch.filter.Property;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface MigrationAssistant<ENTITY> {

    void addProperty(Property<?> property);

    void changePropertyType(Property<?> property, Class<?> from, Class<?> to);

    void renameProperty(String from, String to);

    void removeProperty(String name);

    void createStore();

    void deleteStore();

    void recreateStore();

    boolean storeExists();

}
