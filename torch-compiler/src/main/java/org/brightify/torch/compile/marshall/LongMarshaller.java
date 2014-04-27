package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityMetadataGenerator;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class LongMarshaller extends NumberColumnMarshaller {
    public LongMarshaller() {
        super(Long.class);
    }

    @Override
    protected JExpression fromCursor(EntityMetadataGenerator.CreateFromCursorHolder holder, JVar index,
                                     PropertyMirror propertyMirror) {
        return holder.cursor.invoke("getLong").arg(index);
    }
}
