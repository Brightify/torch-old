package com.brightgestures.brightify.filter;

import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.util.TypeHelper;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface ColumnProvider {

    boolean isSupported(TypeHelper typeHelper, Property property);

    ColumnInfo getColumnInfo(TypeHelper typeHelper, Property property);

}
