package org.brightify.torch.marshall2;

import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.Property;
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
    protected boolean nullable(Property property) {
        return !property.getType().getKind().isPrimitive();
    }

    @Override
    protected JExpression fromCursor(JVar torch, JVar cursor, JVar index, JVar entity, Property property) {
        return cursor.invoke("getInt").arg(index).gt(JExpr.lit(0)); // CURSOR + ".getInt(" + getIndex(property) + ") > 0";
    }
}
