package org.brightify.torch.compile.util;

import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityParseException extends RuntimeException {

    private final List<Element> elements = new ArrayList<Element>();

    public EntityParseException(Element element, String detailMessage, Object... params) {
        this(Collections.singleton(element), detailMessage, params);
    }

    public EntityParseException(Collection<Element> elements, String detailMessage, Object... params) {
        super(String.format(detailMessage, params));

        this.elements.addAll(elements);
    }

    public List<Element> getElements() {
        return elements;
    }
}
