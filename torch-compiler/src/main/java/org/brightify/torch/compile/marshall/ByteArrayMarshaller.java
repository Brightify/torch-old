package org.brightify.torch.compile.marshall;

import com.google.inject.Inject;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.generate.EntityMetadataGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.NoneAffinity;
import org.brightify.torch.compile.util.TypeHelper;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ByteArrayMarshaller extends AbstractMarshaller {

    @Inject
    private TypeHelper typeHelper;

    public ByteArrayMarshaller() {
        super(byte[].class);
    }

    @Override
    protected JExpression fromCursor(EntityMetadataGenerator.CreateFromCursorHolder holder, JVar index,
                                     Property property) {
        return holder.cursor.invoke("getBlob").arg(index);
    }

    @Override
    protected boolean nullable(Property property) {
        return true;
    }

    @Override
    public TypeAffinity getAffinity() {
        return NoneAffinity.getInstance();
    }

    @Override
    protected JClass columnClass(Property property) {
        return CodeModelTypes.GENERIC_PROPERTY.narrow(CodeModelTypes.ref(typeHelper.getWrappedType(property).toString()));
    }

    @Override
    protected JClass columnClassImpl(Property property) {
        return CodeModelTypes.GENERIC_PROPERTY_IMPL.narrow(CodeModelTypes.ref(typeHelper.getWrappedType(property).toString()));
    }
}
