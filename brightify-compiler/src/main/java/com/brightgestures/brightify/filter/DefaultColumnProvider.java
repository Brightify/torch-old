package com.brightgestures.brightify.filter;

import com.brightgestures.brightify.generate.Field;
import com.brightgestures.brightify.generate.GenericField;
import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.util.TypeHelper;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class DefaultColumnProvider implements ColumnProvider {


    @Override
    public boolean isSupported(TypeHelper typeHelper, Property property) {
        return true;
    }

    @Override
    public ColumnInfo getColumnInfo(final TypeHelper typeHelper, Property property) {
        return new ColumnInfo() {
            @Override
            public Field getField(Property property) {
                TypeMirror type = typeHelper.getWrappedType(property);
                Types types = typeHelper.getProcessingEnvironment().getTypeUtils();
                Field childField;
                if (type instanceof DeclaredType) {
                    childField = genericFieldRecursive(types, (DeclaredType) type);
                } else {
                    childField = new Field();
                    childField.setTypeFullName(type.toString());
                }

                final GenericField field = new GenericField(childField);
                field.setTypeFullName(Column.class.getName());
                field.getImports().add("com.brightgestures.brightify.filter.ColumnImpl");
                field.setValue("new ColumnImpl<" + childField.getTypeSimpleName() + ">(\"" + property.columnName + "\");");

                return field;
            }
        };
    }

    private GenericField genericFieldRecursive(Types types, DeclaredType declaredType) {
        List<Field> children = new ArrayList<Field>();
        for (TypeMirror argument : declaredType.getTypeArguments()) {
            if (argument instanceof DeclaredType) {
                children.add(genericFieldRecursive(types, (DeclaredType) argument));
            } else {
                Field childField = new Field();
                childField.setTypeFullName(argument.toString());
                children.add(childField);
            }
        }
        GenericField genericField = new GenericField(children.toArray(new Field[children.size()]));
        genericField.setTypeFullName(types.erasure(declaredType).toString());
        return genericField;
    }

}
