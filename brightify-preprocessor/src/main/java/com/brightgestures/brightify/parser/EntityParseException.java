package com.brightgestures.brightify.parser;

import javax.lang.model.element.Element;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class EntityParseException extends Exception {

    private final Element mElement;

    public EntityParseException(Element element, String detailMessage, Object... params) {
        super(String.format(detailMessage, params));
        mElement = element;
    }

    public Element getElement() {
        return mElement;
    }
}
