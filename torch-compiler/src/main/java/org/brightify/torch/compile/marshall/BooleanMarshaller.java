package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityDescriptionGenerator;
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
    protected JExpression fromRawEntity(EntityDescriptionGenerator.CreateFromRawEntityHolder holder,
                                     PropertyMirror propertyMirror) {
        return holder.rawEntity.invoke("getBoolean").arg(JExpr.lit(propertyMirror.getSafeName()));
    }

    @Override
    protected JClass propertyClass(PropertyMirror propertyMirror) {
        return CodeModelTypes.BOOLEAN_PROPERTY;
    }

    @Override
    protected JClass propertyClassImpl(PropertyMirror propertyMirror) {
        return CodeModelTypes.BOOLEAN_PROPERTY_IMPL;
    }

    @Override
    protected JInvocation propertyClassInvocation(PropertyMirror propertyMirror) {
        JInvocation invocation = JExpr._new(propertyClassImpl(propertyMirror))
                                      .arg(propertyMirror.getName())
                                      .arg(propertyMirror.getSafeName());

        return propertyClassInvocationFeatureParameters(invocation, propertyMirror);
    }
}
