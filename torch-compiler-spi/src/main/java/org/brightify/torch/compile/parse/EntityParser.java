package org.brightify.torch.compile.parse;

import org.brightify.torch.compile.EntityMirror;

import javax.lang.model.element.Element;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface EntityParser {

    EntityMirror parseEntityElement(Element element);

}
