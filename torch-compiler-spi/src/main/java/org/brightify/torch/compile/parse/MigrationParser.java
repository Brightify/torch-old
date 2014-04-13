package org.brightify.torch.compile.parse;

import org.brightify.torch.compile.migration.MigrationMethod;

import javax.lang.model.element.Element;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface MigrationParser {

    Map<Element, MigrationMethod> parseEntityElement(Element element);

}
