package com.brightgestures.brightify.filter;

import com.brightgestures.brightify.generate.Field;
import com.brightgestures.brightify.generate.GenericField;
import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.util.TypeHelper;

import javax.lang.model.util.Types;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class StringColumnProvider implements ColumnProvider {
    @Override
    public boolean isSupported(TypeHelper typeHelper, Property property) {
        Types types = typeHelper.getProcessingEnvironment().getTypeUtils();

        return types.isSameType(typeHelper.typeOf(String.class), property.type);
    }

    @Override
    public ColumnInfo getColumnInfo(TypeHelper typeHelper, Property property) {
        return new ColumnInfo() {
            @Override
            public Field getField(Property property) {
                Field field = new Field();
                field.setTypeFullName(StringColumn.class.getName());
                field.getImports().add("com.brightgestures.brightify.filter.StringColumnImpl");
                field.setValue("new StringColumnImpl(\"" + property.columnName + "\");");
                return field;
            }
        };
    }
}
