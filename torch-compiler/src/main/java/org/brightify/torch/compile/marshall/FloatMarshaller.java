package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.generate.EntityMetadataGenerator;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class FloatMarshaller extends NumberColumnMarshaller {

    public FloatMarshaller() {
        super(Float.class);
    }

    @Override
    protected JExpression fromCursor(EntityMetadataGenerator.CreateFromCursorHolder holder, JVar index,
                                     Property property) {
        return holder.cursor.invoke("getFloat").arg(index);
    }
}
