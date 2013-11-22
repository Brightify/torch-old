package com.brightgestures.brightify.marshall;

import com.brightgestures.brightify.generate.Field;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class SingletonStreamMarshallerInfo implements StreamMarshallerInfo {

    private final String typeFullName;

    public SingletonStreamMarshallerInfo(String typeFullName) {
        this.typeFullName = typeFullName;
    }

    @Override
    public Field getField() {
        Field field = new Field();
        field.setTypeFullName(typeFullName);
        field.setValue(field.getTypeSimpleName() + ".getInstance()");
        return field;
    }
}
