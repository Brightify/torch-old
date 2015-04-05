package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityDescriptionGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class BooleanMarshaller extends AbstractMarshaller {

    public BooleanMarshaller() {
        super(Boolean.class);
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
        return CodeModelTypes.BOOLEAN_PROPERTY.narrow(CodeModelTypes.ref(propertyMirror.getOwner()));
    }

    @Override
    protected JClass propertyClassBase(PropertyMirror propertyMirror) {
        return CodeModelTypes.BOOLEAN_PROPERTY_IMPL.narrow(CodeModelTypes.ref(propertyMirror.getOwner()));
    }

    @Override
    protected JInvocation invokeSuperConstructor(JBlock body, JClass entityClass, PropertyMirror propertyMirror) {
        return body.invoke("super").arg(entityClass.dotclass())
                   .arg(propertyMirror.getName())
                   .arg(propertyMirror.getSafeName());
    }
}
