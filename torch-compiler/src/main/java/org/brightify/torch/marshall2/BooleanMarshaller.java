package org.brightify.torch.marshall2;

import org.brightify.torch.parse.Property;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.IntegerAffinity;

import static org.brightify.torch.generate.MetadataSourceFile.CURSOR;
import static org.brightify.torch.generate.MetadataSourceFile.ENTITY;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class BooleanMarshaller extends AbstractMarshaller<Boolean> {
    @Override
    public TypeAffinity getAffinity() {
        return IntegerAffinity.getInstance();
    }

    @Override
    protected String fromCursor(Property property) {
        return CURSOR + ".getInt(" + getIndex(property) + ") > 0";
    }
}
