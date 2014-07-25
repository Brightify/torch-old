package org.brightify.torch.compile.marshall;

import com.google.inject.Inject;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityDescriptionGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.compile.util.TypeHelper;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.NoneAffinity;

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
    protected JExpression fromRawEntity(EntityDescriptionGenerator.CreateFromRawEntityHolder holder,
                                     PropertyMirror propertyMirror) {
        return holder.rawEntity.invoke("getBlob").arg(JExpr.lit(propertyMirror.getSafeName()));
    }

    @Override
    protected boolean nullable(PropertyMirror propertyMirror) {
        return true;
    }

    @Override
    public TypeAffinity getAffinity() {
        return NoneAffinity.getInstance();
    }

    @Override
    protected JClass propertyClass(PropertyMirror propertyMirror) {
        return CodeModelTypes.GENERIC_PROPERTY.narrow(CodeModelTypes.ref(typeHelper.getWrappedType(propertyMirror).toString()));
    }

    @Override
    protected JClass propertyClassImpl(PropertyMirror propertyMirror) {
        return CodeModelTypes.GENERIC_PROPERTY_IMPL.narrow(CodeModelTypes.ref(typeHelper.getWrappedType(propertyMirror).toString()));
    }
}
