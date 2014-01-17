package com.brightgestures.brightify.filter;

import com.brightgestures.brightify.generate.Field;
import com.brightgestures.brightify.generate.GenericField;
import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.util.TypeHelper;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class NumberColumnProvider implements ColumnProvider {

    private final Map<Class<?>, NumberColumnInfo> numberColumns = new HashMap<>();

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
            Field classField = new Field();
            classField.setTypeFullName(columnClass.getName());

            GenericField field = new GenericField(classField);
            field.setTypeFullName(NumberColumn.class.getName());
            field.getImports().add("com.brightgestures.brightify.filter.NumberColumnImpl");
            field.setValue("new NumberColumnImpl<" + columnClass.getSimpleName() + ">(\"" + property.columnName +
                    "\");");
            return field;
        }
    }

}