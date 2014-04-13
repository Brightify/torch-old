package org.brightify.torch.marshall2;

import com.google.inject.Inject;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.util.TypeHelper;

import javax.lang.model.util.Types;

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
    public boolean accepts(Property property) {
        return types.isSameType(typeHelper.typeOf(acceptedClass), typeHelper.getWrappedType(property));
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public JStatement marshall(JVar torch, JVar contentValues, JVar entity, Property property) {
        return contentValues
                .invoke("put").arg(JExpr.lit(property.getColumnName())).arg(marshallValue(torch, entity, property));
    }

    @Override
    public JStatement unmarshall(JVar torch, JVar cursor, JVar entity, Property property) {
        JBlock block= new JBlock();
        JVar index = block.decl(CodeModelTypes.INTEGER, "index", getIndex(cursor, property));
        JConditional isNull = block._if(index.ne(JExpr._null()));

        isNull._then().add(property.getSetter().setValue(entity, fromCursor(torch, cursor, index, entity, property)));
        if(nullable(property)) {
            isNull._else().add(property.getSetter().setValue(entity, JExpr._null()));
        }
        return block;
    }

    protected JExpression marshallValue(JVar torch, JVar entity, Property property) {
        return property.getGetter().getValue(entity);
    }

    protected abstract JExpression fromCursor(JVar torch, JVar cursor, JVar index, JVar entity, Property property);

    protected abstract boolean nullable(Property property);

    public static JExpression getIndex(JVar cursor, Property property) {
        return cursor.invoke("getColumnIndexOrThrow").arg(JExpr.lit(property.getColumnName()));
    }

}
