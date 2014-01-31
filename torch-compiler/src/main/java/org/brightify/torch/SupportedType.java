package org.brightify.torch;

import org.brightify.torch.parse.Property;
import org.brightify.torch.sql.AbstractTypeAffinity;


/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface SupportedType {
    boolean isSupported(Property property);

    void read(Property property, SourceFileGenerator sourceFileGenerator);

    void write(Property property, SourceFileGenerator sourceFileGenerator);

    Class<? extends AbstractTypeAffinity> getAffinity(Property property);
}
