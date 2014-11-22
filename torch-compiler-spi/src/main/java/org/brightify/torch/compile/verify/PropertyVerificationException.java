package org.brightify.torch.compile.verify;

import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.util.ElementException;

import javax.lang.model.element.Element;
import java.util.Collection;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class PropertyVerificationException extends ElementException {

    public PropertyVerificationException(PropertyMirror propertyMirror, String detailMessage, Object... params) {
        super(propertyMirror.getGetter().getElement(), detailMessage, params);
    }

    public PropertyVerificationException(Element element, String detailMessage, Object... params) {
        super(element, detailMessage, params);
    }

    public PropertyVerificationException(Collection<Element> elements, String detailMessage, Object... params) {
        super(elements, detailMessage, params);
    }
}
