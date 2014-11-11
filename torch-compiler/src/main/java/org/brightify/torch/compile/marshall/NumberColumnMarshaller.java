package org.brightify.torch.compile.marshall;

import com.google.inject.Inject;
import com.sun.codemodel.JClass;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.util.CodeModelTypes;
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
    protected boolean nullable(PropertyMirror propertyMirror) {
        return !propertyMirror.getType().getKind().isPrimitive();
    }

    @Override
    protected JClass propertyClass(PropertyMirror propertyMirror) {
        return CodeModelTypes.NUMBER_PROPERTY.narrow(
                CodeModelTypes.ref(typeHelper.getWrappedType(propertyMirror).toString()));
    }

    @Override
    protected JClass propertyClassImpl(PropertyMirror propertyMirror) {
        return CodeModelTypes.NUMBER_PROPERTY_IMPL.narrow(
                CodeModelTypes.ref(typeHelper.getWrappedType(propertyMirror).toString()));
    }
}