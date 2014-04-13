package org.brightify.torch.compile.marshall;

import com.google.inject.Inject;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.generate.EntityMetadataGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.sql.ColumnDef;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.statement.CreateTable;
import org.brightify.torch.compile.util.TypeHelper;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class AbstractMarshaller implements Marshaller {

    private final Class<?> acceptedClass;

    @Inject
    private TypeHelper typeHelper;

    @Inject
    private Types types;

    public AbstractMarshaller(Class<?> acceptedClass) {
        this.acceptedClass = acceptedClass;
    }

    @Override
    public boolean accepts(TypeMirror type) {
        return types.isSameType(typeHelper.typeOf(acceptedClass), typeHelper.getWrappedType(type));
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public JStatement marshall(EntityMetadataGenerator.ToContentValuesHolder holder, Property property) {
        return holder.contentValues
                .invoke("put").arg(JExpr.lit(property.getColumnName())).arg(marshallValue(holder, property));
    }

    @Override
    public JStatement unmarshall(EntityMetadataGenerator.CreateFromCursorHolder holder, Property property) {
        JBlock block= new JBlock();
        JVar index = block.decl(CodeModelTypes.INTEGER, "index", getIndex(holder.cursor, property));
        JConditional isNull = block._if(index.ne(JExpr._null()));

        isNull._then().add(property.getSetter().setValue(holder.entity, fromCursor(holder, index, property)));
        if(nullable(property)) {
            isNull._else().add(property.getSetter().setValue(holder.entity, JExpr._null()));
        }
        return block;
    }

    @Override
    public ColumnDef createColumn(List<CreateTable> tablesToCreate, Property property) {
        ColumnDef columnDef = new ColumnDef(property.getColumnName());
        columnDef.setTypeAffinity(getAffinity());
        return columnDef;
    }

    @Override
    public JFieldVar createColumnField(JDefinedClass definedClass, Property property) {
        return definedClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, columnClass(property), property.getName(),
                                  columnClassInvocation(property));
    }

    protected JExpression marshallValue(EntityMetadataGenerator.ToContentValuesHolder holder, Property property) {
        return property.getGetter().getValue(holder.entity);
    }

    protected JInvocation columnClassInvocation(Property property) {
        return JExpr._new(columnClassImpl(property)).arg(property.getColumnName());
    }

    public static JExpression getIndex(JVar cursor, Property property) {
        return cursor.invoke("getColumnIndexOrThrow").arg(JExpr.lit(property.getColumnName()));
    }

    protected abstract JExpression fromCursor(EntityMetadataGenerator.CreateFromCursorHolder holder, JVar index,
                                              Property property);

    protected abstract boolean nullable(Property property);

    protected abstract TypeAffinity getAffinity();

    protected abstract JClass columnClass(Property property);

    protected abstract JClass columnClassImpl(Property property);


}
