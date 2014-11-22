package org.brightify.torch.compile.verify;

import com.google.inject.Inject;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.EntityMirror;
import org.brightify.torch.compile.PropertyMirror;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityVerifierImpl implements EntityVerifier {

    @Inject
    private EntityContext entityContext;

    @Inject
    private PropertyVerifier propertyVerifier;

    @Override
    public void verifyEntity(EntityMirror entity) throws EntityVerificationException {
        if(entity.getIdPropertyMirror() == null) {
            throw new EntityVerificationException(entity, "Entity is missing an @Id property.");
        }

        for (PropertyMirror propertyMirror : entity.getProperties()) {
            propertyVerifier.verifyProperty(propertyMirror);
        }
    }
}
