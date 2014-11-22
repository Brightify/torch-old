package org.brightify.torch.compile.parse;

import org.brightify.torch.compile.util.ElementException;

import javax.lang.model.element.Element;
import java.util.Collection;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityParseException extends ElementException {
    public EntityParseException(Element element, String detailMessage, Object... params) {
        super(element, detailMessage, params);
    }

    public EntityParseException(Collection<Element> elements, String detailMessage, Object... params) {
        super(elements, detailMessage, params);
    }
}
