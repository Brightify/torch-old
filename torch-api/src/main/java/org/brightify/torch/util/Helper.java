package org.brightify.torch.util;

import org.brightify.torch.filter.ListProperty;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class Helper {

    public static final String BINDING_TABLE_NAME_GLUE = "____";

    private Helper() {
        throw new UnsupportedOperationException();
    }

    public static String deCamelize(String camelCase) {
        camelCase = camelCase.replaceAll("(.)([A-Z][a-z]+)", "$1_$2");
        camelCase = camelCase.replaceAll("([a-z0-9])([A-Z])", "$1_$2");
        return camelCase.toLowerCase();
    }

    public static String tableNameFromClass(Class<?> cls) {
        return tableNameFromClass(cls, false);
    }

    public static String tableNameFromClass(Class<?> cls, boolean simpleName) {
        if (!simpleName) {
            return tableNameFromClassName(cls.getName());
        } else {
            return tableNameFromClassName(cls.getSimpleName());
        }
    }

    public static String tableNameFromClassName(String name) {
        return name.replace('.', '_');
    }

    public static <ENTITY> String bindingTableNameFromClassAndProperty(Class<ENTITY> entityClass,
                                                                       ListProperty<ENTITY, ?> property) {


        return tableNameFromClass(entityClass) + BINDING_TABLE_NAME_GLUE + property.getName();
    }

}
