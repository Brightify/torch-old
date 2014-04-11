package org.brightify.torch.marshall2;

import org.brightify.torch.marshall.Marshaller2;
import org.brightify.torch.parse.Property;

import static org.brightify.torch.generate.MetadataSourceFile.*;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class AbstractMarshaller<T> implements Marshaller2<T> {

    @Override
    public String marshallingCode(Property property) {
        return CONTENT_VALUES + ".put(\"" + property.getColumnName() + "\", " + ENTITY + "." + property.getValue() + ")";
    }

    @Override
    public String unmarshallingCode(Property property) {
        return ENTITY + "." + property.setValue(fromCursor(property));
    }

    protected String getIndex(Property property) {
        return CURSOR + ".getColumnIndexOrThrow(\"" + property.getColumnName() + "\")";
    }

    protected abstract String fromCursor(Property property);

}
