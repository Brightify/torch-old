package com.brightgestures.droidorm.util;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class Helper {

    private Helper() { }

    public static String deCamelize(String camelCase) {
        camelCase = camelCase.replaceAll("(.)([A-Z][a-z]+)", "$1_$2");
        camelCase = camelCase.replaceAll("([a-z0-9])([A-Z])", "$1_$2");
        return camelCase.toLowerCase();
    }

}
