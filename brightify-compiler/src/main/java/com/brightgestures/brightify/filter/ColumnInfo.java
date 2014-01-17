package com.brightgestures.brightify.filter;

import com.brightgestures.brightify.generate.Field;
import com.brightgestures.brightify.parse.Property;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface ColumnInfo {

    Field getField(Property property);

}
