package org.brightify.torch.filter;

import org.brightify.torch.generate.Field;
import org.brightify.torch.parse.Property;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface ColumnInfo {

    Field getField(Property property);

}
