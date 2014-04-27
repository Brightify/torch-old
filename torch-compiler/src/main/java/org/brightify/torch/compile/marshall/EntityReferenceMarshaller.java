package org.brightify.torch.compile.marshall;

import com.google.inject.Inject;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.brightify.torch.Ref;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityMetadataGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.IntegerAffinity;
import org.brightify.torch.compile.util.TypeHelper;

import javax.annotation.processing.Messager;
import javax.lang.model.type.TypeMirror;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityReferenceMarshaller extends AbstractMarshaller {

    @Inject
    private EntityContext entityContext;

    @Inject
    private Messager messager;

    @Inject
    private TypeHelper typeHelper;

    public EntityReferenceMarshaller() {
        super(Ref.class);
    }

    @Override
    public boolean accepts(TypeMirror type) {
        return entityContext.containsEntity(type.toString());
    }

    @Override
    public int getPriority() {
        // If the object is an entity, we have to be sure this marshaller will be taken before others
        return 10;
    }

    @Override
    protected JExpression marshallValue(EntityMetadataGenerator.ToContentValuesHolder holder, PropertyMirror propertyMirror) {
        JExpression getValue = super.marshallValue(holder, propertyMirror);
        return holder.torch
                .invoke("save")
                .invoke("entity").arg(getValue)
                .invoke("getId");
    }

    @Override
    protected JExpression fromCursor(EntityMetadataGenerator.CreateFromCursorHolder holder, JVar index, PropertyMirror propertyMirror) {
        return holder.torch
                .invoke("load")
                .invoke("type").arg(CodeModelTypes.ref(propertyMirror).dotclass())
                .invoke("id").arg(holder.cursor.invoke("getLong").arg(index));
    }

    @Override
    protected boolean nullable(PropertyMirror propertyMirror) {
        return true;
    }

    @Override
    public TypeAffinity getAffinity() {
        return IntegerAffinity.getInstance();
    }

    @Override
    protected JClass columnClass(PropertyMirror propertyMirror) {
        return CodeModelTypes.GENERIC_PROPERTY.narrow(CodeModelTypes.ref(typeHelper.getWrappedType(propertyMirror).toString()));
    }

    @Override
    protected JClass columnClassImpl(PropertyMirror propertyMirror) {
        return CodeModelTypes.GENERIC_PROPERTY_IMPL.narrow(CodeModelTypes.ref(typeHelper.getWrappedType(propertyMirror)
                                                                                      .toString()));
    }
}
