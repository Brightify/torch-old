package org.brightify.torch.filter;

import org.brightify.torch.generate.Field;
import org.brightify.torch.generate.FieldImpl;
import org.brightify.torch.generate.GenericField;
import org.brightify.torch.parse.Property;
import org.brightify.torch.util.TypeHelper;

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
                FieldImpl childField;
                if (type instanceof DeclaredType) {
                    childField = genericFieldRecursive(types, (DeclaredType) type);
                } else {
                    childField = new FieldImpl();
                    childField.setTypeFullName(type.toString());
                }

                final GenericField field = new GenericField(childField);
                field.setTypeFullName(Column.class.getName());
                field.getImports().add("org.brightify.torch.filter.ColumnImpl");
                field.setValue("new ColumnImpl<" + childField.getTypeSimpleName() + ">(\"" + property.getColumnName() + "\");");

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
                Field childField = new FieldImpl();
                childField.setTypeFullName(argument.toString());
                children.add(childField);
            }
        }
        GenericField genericField = new GenericField(children.toArray(new Field[children.size()]));
        genericField.setTypeFullName(types.erasure(declaredType).toString());
        return genericField;
    }

}
