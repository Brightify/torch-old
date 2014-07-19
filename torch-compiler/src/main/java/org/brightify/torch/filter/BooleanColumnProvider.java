package org.brightify.torch.filter;

import org.brightify.torch.generate.Field;
import org.brightify.torch.generate.FieldImpl;
import org.brightify.torch.parse.Property;
import org.brightify.torch.util.TypeHelper;

import javax.lang.model.util.Types;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class BooleanColumnProvider implements ColumnProvider {
    @Override
    public boolean isSupported(TypeHelper typeHelper, Property property) {
        Types types = typeHelper.getProcessingEnvironment().getTypeUtils();

        return types.isSameType(typeHelper.typeOf(Boolean.class), typeHelper.getWrappedType(property));
    }

    @Override
    public ColumnInfo getColumnInfo(TypeHelper typeHelper, Property property) {
        return new ColumnInfo() {
            @Override
            public Field getField(Property property) {
                Field field = new FieldImpl();
                field.setTypeFullName(BooleanColumn.class.getName());
                field.getImports().add("org.brightify.torch.filter.BooleanColumnImpl");
                field.setValue("new BooleanColumnImpl(\"" + property.getColumnName() + "\");");
                return field;
            }
        };
    }
}
