package org.brightify.torch.marshall2;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.Property;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.TextAffinity;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class StringMarshaller extends AbstractMarshaller {

    public StringMarshaller() {
        super(String.class);
    }

    @Override
    public TypeAffinity getAffinity() {
        return TextAffinity.getInstance();
    }

    @Override
    protected boolean nullable(Property property) {
        return true;
    }

    @Override
    protected JExpression fromCursor(JVar torch, JVar cursor, JVar index, JVar entity, Property property) {
        return cursor.invoke("getString").arg(index);
    }
}
