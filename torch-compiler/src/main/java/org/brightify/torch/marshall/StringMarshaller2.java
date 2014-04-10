package org.brightify.torch.marshall;

import org.brightify.torch.parse.Property;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.TextAffinity;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class StringMarshaller2 implements Marshaller2<String> {
    @Override
    public TypeAffinity getAffinity() {
        return TextAffinity.getInstance();
    }

    @Override
    public String marshallingCode(Property property, String contentValues) {
        return contentValues + ".put(" + property.getColumnName() + ", " + property.getValue();
    }

    @Override
    public String unmarshallingCode(Property property, String cursor) {
        return property.setValue(
                cursor + ".getString(" + cursor + ".getColumnIndexOrThrow(" + property.getColumnName() + "))");
    }
}
