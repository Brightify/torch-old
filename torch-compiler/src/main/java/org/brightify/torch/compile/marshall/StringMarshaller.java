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
public class StringMarshaller extends AbstractMarshaller {

    public StringMarshaller() {
        super(String.class);
    }

    @Override
    protected JClass propertyClass(PropertyMirror propertyMirror) {
        return CodeModelTypes.STRING_PROPERTY.narrow(CodeModelTypes.ref(propertyMirror.getOwner()));
    }

    @Override
    protected JClass propertyClassBase(PropertyMirror propertyMirror) {
        return CodeModelTypes.STRING_PROPERTY_IMPL.narrow(CodeModelTypes.ref(propertyMirror.getOwner()));
    }

    @Override
    protected boolean nullable(PropertyMirror propertyMirror) {
        return true;
    }

    @Override
    protected JExpression fromRawEntity(EntityDescriptionGenerator.CreateFromRawEntityHolder holder,
                                        PropertyMirror propertyMirror) {
        return holder.rawEntity.invoke("getString").arg(JExpr.lit(propertyMirror.getSafeName()));
    }

    @Override
    protected JInvocation invokeSuperConstructor(JBlock body, JClass entityClass, PropertyMirror propertyMirror) {
        return body.invoke("super").arg(entityClass.dotclass())
                   .arg(propertyMirror.getName())
                   .arg(propertyMirror.getSafeName());
    }
}
