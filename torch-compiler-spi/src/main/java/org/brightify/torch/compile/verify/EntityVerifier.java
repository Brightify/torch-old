package org.brightify.torch.compile.verify;

import org.brightify.torch.compile.EntityInfo;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface EntityVerifier {

    void verifyEntity(EntityInfo entity) throws EntityVerificationException;

}
