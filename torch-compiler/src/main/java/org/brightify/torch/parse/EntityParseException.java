package org.brightify.torch.parse;

import javax.lang.model.element.Element;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class EntityParseException extends RuntimeException {

    private final Element mElement;

    public EntityParseException(Element element, String detailMessage, Object... params) {
        super(String.format(detailMessage, params));
        mElement = element;
    }

    public Element getElement() {
        return mElement;
    }
}
