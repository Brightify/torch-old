package org.brightify.torch.marshall2;

import com.google.inject.Inject;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.IntegerAffinity;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityReferenceMarshaller extends AbstractMarshaller {

    @Inject
    private EntityContext entityContext;

    @Inject
    private Messager messager;

    public EntityReferenceMarshaller() {
        super(Object.class);
    }

    @Override
    public TypeAffinity getAffinity() {
        return IntegerAffinity.getInstance();
    }

    @Override
    public boolean accepts(Property property) {
        return entityContext.containsEntity(property.getType().toString());
    }

    @Override
    public int getPriority() {
        // If the object is an entity, we have to be sure this marshaller will be taken before others
        return 10;
    }

    @Override
    protected JExpression marshallValue(JVar torch, JVar entity, Property property) {
        JExpression getValue = super.marshallValue(torch, entity, property);
        return torch
                .invoke("save")
                .invoke("entity").arg(getValue)
                .invoke("getId");
    }

    @Override
    protected JExpression fromCursor(JVar torch, JVar cursor, JVar index, JVar entity, Property property) {
        return torch
                .invoke("load")
                .invoke("type").arg(CodeModelTypes.ref(property).dotclass())
                .invoke("id").arg(cursor.invoke("getLong").arg(index));
    }

    @Override
    protected boolean nullable(Property property) {
        return true;
    }
}
