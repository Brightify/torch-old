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
import org.brightify.torch.compile.PropertyMirror;
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
    public JStatement marshall(EntityMetadataGenerator.ToContentValuesHolder holder, PropertyMirror propertyMirror) {
        return holder.contentValues
                .invoke("put").arg(JExpr.lit(propertyMirror.getColumnName())).arg(marshallValue(holder, propertyMirror));
    }

    @Override
    public JStatement unmarshall(EntityMetadataGenerator.CreateFromCursorHolder holder, PropertyMirror propertyMirror) {
        JBlock block= new JBlock();
        JVar index = block.decl(CodeModelTypes.INTEGER, "index", getIndex(holder.cursor, propertyMirror));
        JConditional isNull = block._if(index.ne(JExpr._null()));

        isNull._then().add(propertyMirror.getSetter().setValue(holder.entity, fromCursor(holder, index, propertyMirror)));
        if(nullable(propertyMirror)) {
            isNull._else().add(propertyMirror.getSetter().setValue(holder.entity, JExpr._null()));
        }
        return block;
    }

    @Override
    public ColumnDef createColumn(EntityMetadataGenerator.ClassHolder holder, List<CreateTable> tablesToCreate, PropertyMirror propertyMirror) {
        ColumnDef columnDef = new ColumnDef(propertyMirror.getColumnName());
        columnDef.setTypeAffinity(getAffinity());
        return columnDef;
    }

    @Override
    public JFieldVar createColumnField(EntityMetadataGenerator.ClassHolder holder, PropertyMirror propertyMirror) {
        return holder.definedClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, columnClass(propertyMirror), propertyMirror.getName(),
                                  columnClassInvocation(propertyMirror));
    }

    protected JExpression marshallValue(EntityMetadataGenerator.ToContentValuesHolder holder, PropertyMirror propertyMirror) {
        return propertyMirror.getGetter().getValue(holder.entity);
    }

    protected JInvocation columnClassInvocation(PropertyMirror propertyMirror) {
        return JExpr._new(columnClassImpl(propertyMirror)).arg(propertyMirror.getColumnName());
    }

    public static JExpression getIndex(JVar cursor, PropertyMirror propertyMirror) {
        return cursor.invoke("getColumnIndexOrThrow").arg(JExpr.lit(propertyMirror.getColumnName()));
    }

    protected abstract JExpression fromCursor(EntityMetadataGenerator.CreateFromCursorHolder holder, JVar index,
                                              PropertyMirror propertyMirror);

    protected abstract boolean nullable(PropertyMirror propertyMirror);

    protected abstract TypeAffinity getAffinity();

    protected abstract JClass columnClass(PropertyMirror propertyMirror);

    protected abstract JClass columnClassImpl(PropertyMirror propertyMirror);


}
