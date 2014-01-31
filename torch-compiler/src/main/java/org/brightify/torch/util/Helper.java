package org.brightify.torch.util;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class Helper {

    private Helper() {
        throw new UnsupportedOperationException();
    }

    public static String deCamelize(String camelCase) {
        camelCase = camelCase.replaceAll("(.)([A-Z][a-z]+)", "$1_$2");
        camelCase = camelCase.replaceAll("([a-z0-9])([A-Z])", "$1_$2");
        return camelCase.toLowerCase();
    }

    public static String tableNameFromClass(Class<?> cls, boolean simpleName) {
        if(!simpleName) {
            return tableNameFromClassName(cls.getName());
        } else {
            return tableNameFromClassName(cls.getSimpleName());
        }
    }

    public static String tableNameFromClassName(String name) {
        return name.replace('.', '_');
    }


}
