package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JStatement;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.generate.EntityMetadataGenerator;
import org.brightify.torch.sql.ColumnDef;
import org.brightify.torch.sql.statement.CreateTable;

import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Marshaller {

    boolean accepts(TypeMirror type);

    int getPriority();

    ColumnDef createColumn(List<CreateTable> tablesToCreate, Property property);

    JFieldVar createColumnField(JDefinedClass definedClass, Property property);

    JStatement marshall(EntityMetadataGenerator.ToContentValuesHolder holder, Property property);

    JStatement unmarshall(EntityMetadataGenerator.CreateFromCursorHolder holder, Property property);
}