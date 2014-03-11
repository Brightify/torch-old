package org.brightify.torch.marshall;

import org.brightify.torch.generate.Field;
import org.brightify.torch.generate.FieldImpl;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class SingletonStreamMarshallerInfo implements StreamMarshallerInfo {

    private final String typeFullName;

    public SingletonStreamMarshallerInfo(String typeFullName) {
        this.typeFullName = typeFullName;
    }

    @Override
    public Field getField() {
        Field field = new FieldImpl();
        field.setTypeFullName(typeFullName);
        field.setValue(field.getTypeSimpleName() + ".getInstance()");
        return field;
    }
}
