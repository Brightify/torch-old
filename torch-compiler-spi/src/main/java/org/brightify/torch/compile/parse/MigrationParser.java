package org.brightify.torch.compile.parse;

import org.brightify.torch.compile.EntityMirror;
import org.brightify.torch.compile.migration.MigrationPath;

import javax.lang.model.element.Element;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface MigrationParser {

    List<MigrationPath> parseEntityElement(Element element, EntityMirror entity);

}
