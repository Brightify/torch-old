package com.brightgestures.brightify.parser;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class AccessorProperty extends Property {

    public String getterName;
    public String getterFullName;
    public String setterName;
    public String setterFullName;

    @Override
    public String setValue(String value) {
        return setterName + "(" + value + ")";
    }

    @Override
    public String getValue() {
        return getterName + "()";
    }
}
