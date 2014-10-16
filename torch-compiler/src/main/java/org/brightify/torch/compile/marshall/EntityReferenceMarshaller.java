package org.brightify.torch.compile.marshall;

import com.google.inject.Inject;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityDescriptionGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;
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
        super(Long.class);
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
    protected JExpression marshallValue(EntityDescriptionGenerator.ToRawEntityHolder holder,
                                        PropertyMirror propertyMirror) {
        JExpression getValue = super.marshallValue(holder, propertyMirror);
        return holder.torchFactory
                .invoke("begin")
                .invoke("save")
                .invoke("entity").arg(getValue)
                .invoke("getId");
    }

    @Override
    protected JExpression fromRawEntity(EntityDescriptionGenerator.CreateFromRawEntityHolder holder,
                                        PropertyMirror propertyMirror) {
        return holder.torchFactory
                .invoke("begin")
                .invoke("load")
                .invoke("type").arg(CodeModelTypes.ref(propertyMirror).dotclass())
                .invoke("id").arg(holder.rawEntity.invoke("getLong").arg(JExpr.lit(propertyMirror.getSafeName())));
    }

    @Override
    protected boolean nullable(PropertyMirror propertyMirror) {
        return true;
    }

    @Override
    protected JClass propertyClass(PropertyMirror propertyMirror) {
        return CodeModelTypes.GENERIC_PROPERTY.narrow(CodeModelTypes.LONG); // CodeModelTypes.ref(typeHelper
        // .getWrappedType(propertyMirror)
        //       .toString()));
    }

    @Override
    protected JClass propertyClassImpl(PropertyMirror propertyMirror) {
        return CodeModelTypes.GENERIC_PROPERTY_IMPL.narrow(CodeModelTypes.LONG); //CodeModelTypes.ref(typeHelper
        // .getWrappedType(propertyMirror)
        //                             .toString()));
    }
}
