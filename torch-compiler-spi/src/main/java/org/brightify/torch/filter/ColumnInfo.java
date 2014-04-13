package org.brightify.torch.filter;

import org.brightify.torch.generate.Field;
import org.brightify.torch.compile.Property;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ColumnInfo {

    Field getField(Property property);

}
