package org.brightify.torch.marshall2;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.Property;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.RealAffinity;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class FloatMarshaller extends AbstractMarshaller {

    public FloatMarshaller() {
        super(Float.class);
    }

    @Override
    public TypeAffinity getAffinity() {
        return RealAffinity.getInstance();
    }

    @Override
    protected boolean nullable(Property property) {
        return !property.getType().getKind().isPrimitive();
    }

    @Override
    protected JExpression fromCursor(JVar torch, JVar cursor, JVar index, JVar entity, Property property) {
        return cursor.invoke("getFloat").arg(index);
    }
}
