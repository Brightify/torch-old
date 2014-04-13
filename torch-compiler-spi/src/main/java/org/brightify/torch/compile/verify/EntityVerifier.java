package org.brightify.torch.compile.verify;

import org.brightify.torch.compile.EntityMirror;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface EntityVerifier {

    void verifyEntity(EntityMirror entity) throws EntityVerificationException;

}
