package org.brightify.torch.marshall2;

import org.brightify.torch.parse.Property;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.RealAffinity;

import static org.brightify.torch.generate.MetadataSourceFile.CURSOR;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class DoubleMarshaller extends AbstractMarshaller<Double> {
    @Override
    public TypeAffinity getAffinity() {
        return RealAffinity.getInstance();
    }

    @Override
    protected String fromCursor(Property property) {
        return CURSOR + ".getDouble(" + getIndex(property) + ")";
    }
}
