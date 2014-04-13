package org.brightify.torch.util;

import org.brightify.torch.filter.Property;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface MigrationAssistant<ENTITY> {

    void addColumn(Property<?> property);

    void changeColumnType(Property<?> property, Class<?> from, Class<?> to);

    void renameColumn(String from, String to);

    void removeColumn(String name);

}
