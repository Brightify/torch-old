package org.brightify.torch.marshall;

import org.brightify.torch.parse.Property;
import org.brightify.torch.sql.TypeAffinity;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Marshaller2<T> {

    TypeAffinity getAffinity();

    String marshallingCode(Property property, String contentValues);

    String unmarshallingCode(Property property, String cursor);

}