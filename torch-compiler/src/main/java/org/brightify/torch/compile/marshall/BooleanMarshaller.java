package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityMetadataGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.IntegerAffinity;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class BooleanMarshaller extends AbstractMarshaller {

    public BooleanMarshaller() {
        super(Boolean.class);
    }

    @Override
    public TypeAffinity getAffinity() {
        return IntegerAffinity.getInstance();
    }

    @Override
    protected boolean nullable(PropertyMirror propertyMirror) {
        return !propertyMirror.getType().getKind().isPrimitive();
    }

    @Override
    protected JExpression fromCursor(EntityMetadataGenerator.CreateFromCursorHolder holder, JVar index,
                                     PropertyMirror propertyMirror) {
        return holder.cursor.invoke("getInt").arg(index).gt(JExpr.lit(0));
    }

    @Override
    protected JClass columnClass(PropertyMirror propertyMirror) {
        return CodeModelTypes.BOOLEAN_PROPERTY;
    }

    @Override
    protected JClass columnClassImpl(PropertyMirror propertyMirror) {
        return CodeModelTypes.BOOLEAN_PROPERTY_IMPL;
    }
}
