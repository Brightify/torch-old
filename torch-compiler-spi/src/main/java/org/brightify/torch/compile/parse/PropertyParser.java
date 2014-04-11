package org.brightify.torch.compile.parse;

import org.brightify.torch.compile.Property;

import javax.lang.model.element.Element;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface PropertyParser {

    Map<String, Property<?>> parseEntityElement(Element element);

}
