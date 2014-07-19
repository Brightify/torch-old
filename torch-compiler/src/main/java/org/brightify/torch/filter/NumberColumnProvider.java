package org.brightify.torch.filter;

import org.brightify.torch.generate.Field;
import org.brightify.torch.generate.FieldImpl;
import org.brightify.torch.generate.GenericField;
import org.brightify.torch.parse.Property;
import org.brightify.torch.util.TypeHelper;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class NumberColumnProvider implements ColumnProvider {

    private final Map<Class<?>, NumberColumnInfo> numberColumns = new HashMap<Class<?>, NumberColumnInfo>();

    public NumberColumnProvider() {
        addColumn(Byte.class);
        addColumn(Short.class);
        addColumn(Integer.class);
        addColumn(Long.class);

        addColumn(Float.class);
        addColumn(Double.class);
    }

    private void addColumn(Class<?> columnClass) {
        numberColumns.put(columnClass, new NumberColumnInfo(columnClass));
    }

    @Override
    public boolean isSupported(TypeHelper typeHelper, Property property) {
        return getColumnInfo(typeHelper, property) != null;
    }

    @Override
    public ColumnInfo getColumnInfo(TypeHelper typeHelper, Property property) {
        TypeMirror propertyType = typeHelper.getWrappedType(property);
        Types typeUtils = typeHelper.getProcessingEnvironment().getTypeUtils();
        for (Class<?> cls : numberColumns.keySet()) {
            if (typeUtils.isSameType(typeHelper.typeOf(cls), propertyType)) {
                return numberColumns.get(cls);
            }
        }
        return null;
    }

    private class NumberColumnInfo implements ColumnInfo {
        private final Class<?> columnClass;

        public NumberColumnInfo(Class<?> columnClass) {
            this.columnClass = columnClass;

        }

        @Override
        public Field getField(Property property) {
            FieldImpl classField = new FieldImpl();
            classField.setTypeFullName(columnClass.getName());

            GenericField field = new GenericField(classField);
            field.setTypeFullName(NumberColumn.class.getName());
            field.getImports().add("org.brightify.torch.filter.NumberColumnImpl");
            field.setValue("new NumberColumnImpl<" + columnClass.getSimpleName() + ">(\"" + property.getColumnName() +
                    "\");");
            return field;
        }
    }

}