package org.brightify.torch.compile.verify;

import com.google.inject.Inject;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.util.TypeHelper;

import javax.lang.model.util.Types;

public class PropertyVerifierImpl implements PropertyVerifier {

    @Inject
    private TypeHelper typeHelper;

    @Inject
    private Types types;

    @Override
    public void verifyProperty(PropertyMirror property) throws EntityVerificationException {
        verifyId(property);
    }

    private void verifyId(PropertyMirror property) {
        if(property.getId() == null) {
            return;
        }

        if(!types.isSameType(property.getType(), typeHelper.typeOf(Long.class))) {
            throw new PropertyVerificationException(property, "@Id property has to be type Long.");
        }
    }
}
