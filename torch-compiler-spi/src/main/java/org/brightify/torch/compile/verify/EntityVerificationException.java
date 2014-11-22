package org.brightify.torch.compile.verify;

import org.brightify.torch.compile.EntityMirror;
import org.brightify.torch.compile.util.ElementException;

import javax.lang.model.element.Element;
import java.util.Collection;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityVerificationException extends ElementException {


    public EntityVerificationException(EntityMirror entityMirror, String detailMessage, Object... params) {
        super(entityMirror.getElement(), detailMessage, params);
    }

    public EntityVerificationException(Collection<Element> elements, String detailMessage, Object... params) {
        super(elements, detailMessage, params);
    }
}
