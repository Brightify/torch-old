package org.brightify.torch.compile.marshall;

import com.google.inject.Inject;
import com.sun.codemodel.JClass;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.IntegerAffinity;
import org.brightify.torch.compile.util.TypeHelper;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class NumberColumnMarshaller extends AbstractMarshaller {

    @Inject
    private TypeHelper typeHelper;

    public NumberColumnMarshaller(Class<?> acceptedClass) {
        super(acceptedClass);
    }

    @Override
    protected boolean nullable(Property property) {
        return !property.getType().getKind().isPrimitive();
    }

    @Override
    protected TypeAffinity getAffinity() {
        return IntegerAffinity.getInstance();
    }

    @Override
    protected JClass columnClass(Property property) {
        return CodeModelTypes.NUMBER_PROPERTY.narrow(CodeModelTypes.ref(typeHelper.getWrappedType(property).toString()));
    }

    @Override
    protected JClass columnClassImpl(Property property) {
        return CodeModelTypes.NUMBER_PROPERTY_IMPL.narrow(
                CodeModelTypes.ref(typeHelper.getWrappedType(property).toString()));
    }
}
