package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityDescriptionGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class FloatMarshaller extends NumberColumnMarshaller {

    public FloatMarshaller() {
        super(Float.class);
    }

    @Override
    protected JExpression fromRawEntity(EntityDescriptionGenerator.CreateFromRawEntityHolder holder,
                                     PropertyMirror propertyMirror) {
        return holder.rawEntity.invoke("getFloat").arg(JExpr.lit(propertyMirror.getSafeName()));
    }

    @Override
    protected JClass propertyClassBase(PropertyMirror propertyMirror) {
        return CodeModelTypes.FLOAT_PROPERTY_IMPL
                .narrow(CodeModelTypes.ref(propertyMirror.getOwner()));
    }
}
