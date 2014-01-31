package org.brightify.torch.filter;

import org.brightify.torch.parse.Property;
import org.brightify.torch.util.TypeHelper;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface ColumnProvider {

    boolean isSupported(TypeHelper typeHelper, Property property);

    ColumnInfo getColumnInfo(TypeHelper typeHelper, Property property);

}
