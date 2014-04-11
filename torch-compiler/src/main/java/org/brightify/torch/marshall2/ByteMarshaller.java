package org.brightify.torch.marshall2;

import org.brightify.torch.parse.Property;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.IntegerAffinity;

import static org.brightify.torch.generate.MetadataSourceFile.CURSOR;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ByteMarshaller extends AbstractMarshaller<Byte> {

    @Override
    public TypeAffinity getAffinity() {
        return IntegerAffinity.getInstance();
    }

    @Override
    protected String fromCursor(Property property) {
        return "(Byte) " + CURSOR + ".getInt(" + getIndex(property) + ")";
    }
}
