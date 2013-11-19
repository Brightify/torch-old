package com.brightgestures.brightify;

import com.brightgestures.brightify.SourceFileGenerator;
import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.sql.TypeName;


/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface SupportedType {
    boolean isSupported(Property property);

    void read(Property property, SourceFileGenerator sourceFileGenerator);

    void write(Property property, SourceFileGenerator sourceFileGenerator);

    Class<? extends TypeName> getAffinity(Property property);
}
