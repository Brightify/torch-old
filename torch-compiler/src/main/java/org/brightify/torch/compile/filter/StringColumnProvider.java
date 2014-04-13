package org.brightify.torch.compile.filter;

import com.sun.codemodel.JClass;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.util.CodeModelTypes;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class StringColumnProvider extends AbstractColumnProvider {

    public StringColumnProvider() {
        super(String.class);
    }

    @Override
    protected JClass columnClass(Property property) {
        return CodeModelTypes.STRING_COLUMN;
    }

    @Override
    protected JClass columnClassImpl(Property property) {
        return CodeModelTypes.STRING_COLUMN_IMPL;
    }
}
