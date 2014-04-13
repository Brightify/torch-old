package org.brightify.torch.compile.filter;

import com.google.inject.Inject;
import com.sun.codemodel.JClass;
import org.brightify.torch.compile.filter.ColumnProvider;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.filter.ColumnInfo;
import org.brightify.torch.filter.GenericColumn;
import org.brightify.torch.generate.Field;
import org.brightify.torch.generate.FieldImpl;
import org.brightify.torch.generate.GenericField;
import org.brightify.torch.compile.Property;
import org.brightify.torch.util.TypeHelper;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class DefaultColumnProvider extends AbstractColumnProvider {

    @Inject
    private TypeHelper typeHelper;

    @Override
    public boolean accepts(Property property) {
        return true;
    }

    @Override
    public int getPriority() {
        // This is a fallback column provider, the priority has to be the lowest
        return Integer.MIN_VALUE;
    }

    @Override
    protected JClass columnClass(Property property) {
        return CodeModelTypes.GENERIC_COLUMN.narrow(CodeModelTypes.ref(typeHelper.getWrappedType(property).toString()));
    }

    @Override
    protected JClass columnClassImpl(Property property) {
        return CodeModelTypes.GENERIC_COLUMN_IMPL.narrow(CodeModelTypes.ref(typeHelper.getWrappedType(property).toString()));
    }
}
