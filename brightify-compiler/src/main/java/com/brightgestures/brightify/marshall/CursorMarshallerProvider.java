package com.brightgestures.brightify.marshall;

import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.util.TypeHelper;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface CursorMarshallerProvider {

    boolean isSupported(TypeHelper typeHelper, Property property);

    CursorMarshallerInfo getMarshallerInfo(TypeHelper typeHelper, Property property);

}
