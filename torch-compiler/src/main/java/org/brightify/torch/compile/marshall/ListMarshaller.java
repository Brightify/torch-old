package org.brightify.torch.compile.marshall;

import com.google.inject.Inject;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.generate.EntityMetadataGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.compile.util.TypeHelper;
import org.brightify.torch.sql.ColumnDef;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.IntegerAffinity;
import org.brightify.torch.sql.statement.CreateTable;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ListMarshaller extends AbstractMarshaller {

    @Inject
    private Types types;

    @Inject
    private TypeHelper typeHelper;

    @Inject
    private MarshallerRegistry marshallerRegistry;

    @Inject
    private EntityContext entityContext;

    public ListMarshaller() {
        super(List.class);
    }

    @Override
    public boolean accepts(TypeMirror type) {
        DeclaredType list = types.getDeclaredType(typeHelper.elementOf(List.class), types.getWildcardType(null, null));

        if(!types.isAssignable(typeHelper.getWrappedType(type), list)) {
            return false;
        }

        TypeMirror listType = typeHelper.singleGenericParameter(type);

        return marshallerRegistry.getMarshaller(listType) != null && !entityContext.containsEntity(listType.toString());
    }

    @Override
    public JStatement marshall(EntityMetadataGenerator.ToContentValuesHolder holder, Property property) {
        // FIXME foreach item in list add torch
        return new JBlock();
    }

    @Override
    protected JExpression marshallValue(EntityMetadataGenerator.ToContentValuesHolder holder, Property property) {
        // Not used
        return JExpr._null();
    }

    @Override
    protected JExpression fromCursor(EntityMetadataGenerator.CreateFromCursorHolder holder, JVar index, Property property) {


        return holder.torch
                .invoke("relation")
                .invoke("with").arg(CodeModelTypes.ref(property).dotclass())
                .invoke("onProperty").arg(holder.classHolder.definedClass.staticRef(property.getColumnName()));
    }

    @Override
    public ColumnDef createColumn(List<CreateTable> tablesToCreate, Property property) {



        return null;
    }

    @Override
    protected boolean nullable(Property property) {
        return true;
    }

    @Override
    protected TypeAffinity getAffinity() {
        return IntegerAffinity.getInstance();
    }

    @Override
    protected JClass columnClass(Property property) {
        return CodeModelTypes.LIST_PROPERTY.narrow(CodeModelTypes.ref(property));
    }

    @Override
    protected JClass columnClassImpl(Property property) {
        return CodeModelTypes.LIST_PROPERTY_IMPL.narrow(CodeModelTypes.ref(property));
    }
}
