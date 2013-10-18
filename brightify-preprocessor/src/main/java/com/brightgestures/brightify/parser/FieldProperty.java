package com.brightgestures.brightify.parser;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class FieldProperty extends Property {

    public String name;
    public String fullName;

    @Override
    public String setValue(String value) {
        return name + " = " + value;
    }

    @Override
    public String getValue() {
        return name;
    }
}
