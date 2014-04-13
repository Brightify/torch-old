package org.brightify.torch.marshall2;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.Property;
import org.brightify.torch.sql.TypeAffinity;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Marshaller {

    TypeAffinity getAffinity();

    boolean accepts(Property property);

    int getPriority();

    JStatement marshall(JVar torch, JVar contentValues, JVar entity, Property property);

    JStatement unmarshall(JVar torch, JVar cursor, JVar entity, Property property);

}