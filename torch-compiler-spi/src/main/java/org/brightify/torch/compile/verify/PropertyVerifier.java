package org.brightify.torch.compile.verify;

import org.brightify.torch.compile.PropertyMirror;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface PropertyVerifier {

    void verifyProperty(PropertyMirror property) throws EntityVerificationException;

}
