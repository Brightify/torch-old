package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JStatement;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityDescriptionGenerator;
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

    ColumnDef createColumn(EntityDescriptionGenerator.ClassHolder holder, List<CreateTable> tablesToCreate,
                           PropertyMirror propertyMirror);

    JFieldVar createPropertyField(EntityDescriptionGenerator.ClassHolder holder, PropertyMirror propertyMirror);

    JStatement marshall(EntityDescriptionGenerator.ToRawEntityHolder holder, PropertyMirror propertyMirror);

    JStatement unmarshall(EntityDescriptionGenerator.CreateFromRawEntityHolder holder, PropertyMirror propertyMirror);
}