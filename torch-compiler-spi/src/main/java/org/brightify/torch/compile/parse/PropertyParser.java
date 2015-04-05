package org.brightify.torch.compile.parse;

import org.brightify.torch.compile.EntityMirror;
import org.brightify.torch.compile.PropertyMirror;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface PropertyParser {

    List<PropertyMirror> parseEntity(EntityMirror entity);

}
