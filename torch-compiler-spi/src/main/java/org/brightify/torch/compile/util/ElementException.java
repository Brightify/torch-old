package org.brightify.torch.compile.util;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ElementException extends RuntimeException {

    private final List<Element> elements = new ArrayList<Element>();

    public ElementException(Element element, String detailMessage, Object... params) {
        this(Collections.singleton(element), detailMessage, params);
    }

    public ElementException(Collection<Element> elements, String detailMessage, Object... params) {
        super(String.format(detailMessage, params));

        this.elements.addAll(elements);
    }

    public List<Element> getElements() {
        return elements;
    }

    public void print(Messager messager) {
        if (getElements().size() > 0) {
            for (Element elementInError : getElements()) {
                messager.printMessage(Diagnostic.Kind.ERROR, getMessage(), elementInError);
            }
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, getMessage());
        }
    }
}
