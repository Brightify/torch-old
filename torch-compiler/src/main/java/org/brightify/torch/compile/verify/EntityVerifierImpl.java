package org.brightify.torch.compile.verify;

import com.google.inject.Inject;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.EntityMirror;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityVerifierImpl implements EntityVerifier {

    @Inject
    private EntityContext entityContext;

    @Override
    public void verifyEntity(EntityMirror entity) throws EntityVerificationException {

    }
}
