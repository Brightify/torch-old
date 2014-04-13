package org.brightify.torch.compile.filter;

import com.google.inject.Inject;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import org.brightify.torch.compile.filter.ColumnProvider;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.filter.ColumnInfo;
import org.brightify.torch.filter.NumberColumn;
import org.brightify.torch.generate.Field;
import org.brightify.torch.generate.FieldImpl;
import org.brightify.torch.generate.GenericField;
import org.brightify.torch.compile.Property;
import org.brightify.torch.util.TypeHelper;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class NumberColumnProvider extends AbstractColumnProvider {

    @Inject
    private TypeHelper typeHelper;

    public NumberColumnProvider() {
        super(Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class);
    }

    @Override
    protected JClass columnClass(Property property) {
        return CodeModelTypes.NUMBER_COLUMN.narrow(CodeModelTypes.ref(typeHelper.getWrappedType(property).toString()));
    }

    @Override
    protected JClass columnClassImpl(Property property) {
        return CodeModelTypes.NUMBER_COLUMN_IMPL.narrow(CodeModelTypes.ref(typeHelper.getWrappedType(property).toString()));
    }

}