package org.brightify.torch.util;

import org.brightify.torch.filter.ListProperty;

import java.util.Arrays;
import java.util.List;

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

    public static String stringsToString(String[] strings, int offset, String delimiter) {
        return stringsToString(strings, offset, strings.length - offset, delimiter);
    }

    public static String stringsToString(String[] strings, int offset, int length, String delimiter) {
        String[] subArray = new String[length];
        System.arraycopy(strings, offset, subArray, 0, length);
        return stringsToString(subArray, delimiter);
    }

    public static String stringsToString(String[] strings, String delimiter) {
        return stringsToString(Arrays.asList(strings), delimiter);
    }

    public static String stringsToString(List<String> strings, String delimiter) {
        StringBuilder builder = new StringBuilder();
        appendStrings(strings, builder, delimiter);
        return builder.toString();
    }

    public static void appendStrings(Iterable<String> strings, StringBuilder builder, String delimiter) {
        String localDelimiter = "";
        for (String string : strings) {
            builder.append(localDelimiter).append(string);
            if(localDelimiter.equals("")) {
                localDelimiter = delimiter;
            }
        }
    }

    public static String safeNameFromClass(Class<?> cls) {
        return safeNameFromClass(cls, false);
    }

    public static String safeNameFromClass(Class<?> cls, boolean simpleName) {
        if (!simpleName) {
            return safeNameFromClassName(cls.getCanonicalName());
        } else {
            return safeNameFromClassName(cls.getSimpleName());
        }
    }

    public static String safeNameFromClassName(String name) {
        return name.replace('.', '_');
    }

    public static <ENTITY> String bindingTableNameFromClassAndProperty(Class<ENTITY> entityClass,
                                                                       ListProperty property) {


        return bindingTableName(entityClass.getCanonicalName(), property.getName());
    }

    public static String bindingTableName(String className, String propertyName) {
        return safeNameFromClassName(className) + BINDING_TABLE_NAME_GLUE + propertyName;
    }

}
