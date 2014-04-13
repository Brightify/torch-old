package org.brightify.torch.compile.filter;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.util.CodeModelTypes;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class BooleanColumnProvider extends AbstractColumnProvider {

    public BooleanColumnProvider() {
        super(Boolean.class);
    }

    @Override
    protected JClass columnClass(Property property) {
        return CodeModelTypes.BOOLEAN_COLUMN;
    }

    @Override
    protected JClass columnClassImpl(Property property) {
        return CodeModelTypes.BOOLEAN_COLUMN_IMPL;
    }

}