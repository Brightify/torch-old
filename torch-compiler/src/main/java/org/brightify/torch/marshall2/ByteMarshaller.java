package org.brightify.torch.marshall2;

import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.IntegerAffinity;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ByteMarshaller extends AbstractMarshaller {

    public ByteMarshaller() {
        super(Byte.class);
    }

    @Override
    public TypeAffinity getAffinity() {
        return IntegerAffinity.getInstance();
    }

    @Override
    protected boolean nullable(Property property) {
        return !property.getType().getKind().isPrimitive();
    }

    @Override
    protected JExpression fromCursor(JVar torch, JVar cursor, JVar index, JVar entity, Property property) {
        return JExpr.cast(CodeModelTypes.BYTE, cursor.invoke("getInt").arg(index));
    }
}
