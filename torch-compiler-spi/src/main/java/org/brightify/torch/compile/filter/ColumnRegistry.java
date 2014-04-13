package org.brightify.torch.compile.filter;

import org.brightify.torch.compile.Property;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ColumnRegistry {

    ColumnProvider getColumnProvider(Property property);

}
