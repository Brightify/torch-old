package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JStatement;
import org.brightify.torch.compile.PropertyMirror;
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

    ColumnDef createColumn(EntityMetadataGenerator.ClassHolder holder, List<CreateTable> tablesToCreate,
                           PropertyMirror propertyMirror);

    JFieldVar createColumnField(EntityMetadataGenerator.ClassHolder holder, PropertyMirror propertyMirror);

    JStatement marshall(EntityMetadataGenerator.ToContentValuesHolder holder, PropertyMirror propertyMirror);

    JStatement unmarshall(EntityMetadataGenerator.CreateFromCursorHolder holder, PropertyMirror propertyMirror);
}