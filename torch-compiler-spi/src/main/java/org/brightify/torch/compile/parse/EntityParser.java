package org.brightify.torch.compile.parse;

import org.brightify.torch.compile.EntityInfo;

import javax.lang.model.element.Element;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface EntityParser {

    EntityInfo parseEntityElement(Element element);

}
