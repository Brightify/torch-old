package org.brightify.torch.marshall;

import org.brightify.torch.parse.Property;
import org.brightify.torch.util.TypeHelper;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface CursorMarshallerProvider {

    boolean isSupported(TypeHelper typeHelper, Property property);

    CursorMarshallerInfo getMarshallerInfo(TypeHelper typeHelper, Property property);

}
