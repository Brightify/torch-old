package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.generate.EntityMetadataGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;
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
    protected JClass columnClass(Property property) {
        return CodeModelTypes.STRING_PROPERTY;
    }

    @Override
    protected JClass columnClassImpl(Property property) {
        return CodeModelTypes.STRING_PROPERTY_IMPL;
    }

    @Override
    protected boolean nullable(Property property) {
        return true;
    }

    @Override
    protected JExpression fromCursor(EntityMetadataGenerator.CreateFromCursorHolder holder, JVar index,
                                     Property property) {
        return holder.cursor.invoke("getString").arg(index);
    }
}
