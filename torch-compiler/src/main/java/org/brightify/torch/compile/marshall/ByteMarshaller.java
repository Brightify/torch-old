package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityMetadataGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ByteMarshaller extends NumberColumnMarshaller {

    public ByteMarshaller() {
        super(Byte.class);
    }

    @Override
    protected JExpression fromCursor(EntityMetadataGenerator.CreateFromCursorHolder holder, JVar index,
                                     PropertyMirror propertyMirror) {
        return JExpr.cast(CodeModelTypes.BYTE, holder.cursor.invoke("getInt").arg(index));
    }
}
