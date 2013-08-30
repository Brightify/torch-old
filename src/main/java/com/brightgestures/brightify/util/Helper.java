package com.brightgestures.brightify.util;

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
        String name = deCamelize(cls.getSimpleName());
        if(!simpleName) {
            String packageName = cls.getPackage().getName().replace('.', '_');

            name = deCamelize(packageName) + "_" + name;
        }
        return name;
    }

}
