package org.brightify.torch.compile.filter;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import org.brightify.torch.compile.Property;
import org.brightify.torch.filter.ColumnInfo;
import org.brightify.torch.util.TypeHelper;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ColumnProvider {

    boolean accepts(Property property);

    int getPriority();

    JFieldVar createColumnField(JDefinedClass definedClass, Property property);

}
