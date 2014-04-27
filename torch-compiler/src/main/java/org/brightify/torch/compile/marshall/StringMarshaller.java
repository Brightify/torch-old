package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.PropertyMirror;
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
    protected JClass columnClass(PropertyMirror propertyMirror) {
        return CodeModelTypes.STRING_PROPERTY;
    }

    @Override
    protected JClass columnClassImpl(PropertyMirror propertyMirror) {
        return CodeModelTypes.STRING_PROPERTY_IMPL;
    }

    @Override
    protected boolean nullable(PropertyMirror propertyMirror) {
        return true;
    }

    @Override
    protected JExpression fromCursor(EntityMetadataGenerator.CreateFromCursorHolder holder, JVar index,
                                     PropertyMirror propertyMirror) {
        return holder.cursor.invoke("getString").arg(index);
    }
}
