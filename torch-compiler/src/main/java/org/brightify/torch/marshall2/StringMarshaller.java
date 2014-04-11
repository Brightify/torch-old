package org.brightify.torch.marshall2;

import org.brightify.torch.parse.Property;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.TextAffinity;

import static org.brightify.torch.generate.MetadataSourceFile.CURSOR;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class StringMarshaller extends AbstractMarshaller<String> {
    @Override
    public TypeAffinity getAffinity() {
        return TextAffinity.getInstance();
    }

    @Override
    protected String fromCursor(Property property) {
        return CURSOR + ".getString(" + getIndex(property) + ")";
    }
}
