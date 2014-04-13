package org.brightify.torch.marshall2;

import org.brightify.torch.compile.Property;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface MarshallerProvider {

    Marshaller getMarshaller(Property property);

    Marshaller getMarshallerOrThrow(Property property);

}
