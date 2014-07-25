package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityDescriptionGenerator;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ShortMarshaller extends NumberColumnMarshaller {

    public ShortMarshaller() {
        super(Short.class);
    }

    @Override
    protected JExpression fromRawEntity(EntityDescriptionGenerator.CreateFromRawEntityHolder holder,
                                     PropertyMirror propertyMirror) {
        return holder.rawEntity.invoke("getShort").arg(JExpr.lit(propertyMirror.getSafeName()));
    }
}
