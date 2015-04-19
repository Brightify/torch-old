package org.brightify.torch.compile.marshall;

import com.google.inject.Inject;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JOp;
import org.brightify.torch.RefCollection;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityDescriptionGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.compile.util.TypeHelper;

import javax.annotation.processing.Messager;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Collection;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ReferenceCollectionMarshaller extends AbstractMarshaller {

    @Inject
    private EntityContext entityContext;

    @Inject
    private Messager messager;

    @Inject
    private Types types;

    @Inject
    private TypeHelper typeHelper;

    public ReferenceCollectionMarshaller() {
        super(RefCollection.class);
    }

    @Override
    public boolean accepts(TypeMirror type) {
        //DeclaredType refCollection = types.getDeclaredType(typeHelper.elementOf(RefCollection.class), types.getWildcardType(null, null));
        DeclaredType refCollection = types.getDeclaredType(typeHelper.elementOf(Collection.class), types.getWildcardType(null, null));

        if (!types.isAssignable(typeHelper.getWrappedType(type), refCollection)) {
            return false;
        }

        TypeMirror refType = typeHelper.singleGenericParameter(type);

        return entityContext.containsEntity(refType.toString());
    }

    @Override
    protected JClass getBackingType(PropertyMirror propertyMirror) {
        return CodeModelTypes.COLLECTION
                .narrow(CodeModelTypes.ref(typeHelper.singleGenericParameter(propertyMirror.getType()).toString()));
    }

    @Override
    public PropertyType getPropertyType() {
        return PropertyType.REFERENCE_COLLECTION;
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    protected JExpression marshallValue(EntityDescriptionGenerator.ToRawEntityHolder holder,
                                        PropertyMirror propertyMirror) {
        JExpression getValue = super.marshallValue(holder, propertyMirror);

        JExpression saveEntity = JOp.cond(JOp.eq(getValue.invoke("isLoaded"), JExpr.TRUE), holder.saveContainer
                .invoke("getTorchFactory")
                .invoke("begin")
                .invoke("save")
                .invoke("entity").arg(getValue), getValue.invoke("getEntityId"));

        return JOp.cond(JOp.ne(getValue, JExpr._null()), saveEntity, JExpr._null());
    }

    @Override
    protected JExpression fromRawEntity(EntityDescriptionGenerator.CreateFromRawEntityHolder holder,
                                        PropertyMirror propertyMirror) {
        JExpression getEntityId = holder.rawEntity.invoke("getLong").arg(JExpr.lit(propertyMirror.getSafeName()));

        TypeMirror childType = typeHelper.singleGenericParameter(propertyMirror.getType());

        return holder.loadContainer
                .invoke("requestReferenceCollection").arg(getEntityId).arg(CodeModelTypes.ref(childType.toString()).dotclass());
    }

    @Override
    protected boolean nullable(PropertyMirror propertyMirror) {
        return true;
    }

    @Override
    protected JClass propertyClass(PropertyMirror propertyMirror) {
        return CodeModelTypes.REFERENCE_COLLECTION_PROPERTY
                .narrow(CodeModelTypes.ref(propertyMirror.getOwner()))
                .narrow(CodeModelTypes.ref(typeHelper.singleGenericParameter(typeHelper.getWrappedType(propertyMirror)).toString()));
    }

    @Override
    protected JClass propertyClassBase(PropertyMirror propertyMirror) {
        return CodeModelTypes.REFERENCE_COLLECTION_PROPERTY_IMPL
                .narrow(CodeModelTypes.ref(propertyMirror.getOwner()))
                .narrow(CodeModelTypes.ref(typeHelper.singleGenericParameter(typeHelper.getWrappedType(propertyMirror)).toString()));
    }

    @Override
    protected JInvocation invokeSuperConstructor(JBlock body, JClass entityClass, PropertyMirror propertyMirror) {
        return body.invoke("super").arg(entityClass.dotclass())
                .arg(CodeModelTypes.ref(typeHelper.singleGenericParameter(propertyMirror.getType()).toString())
                                   .dotclass())
                .arg(propertyMirror.getName())
                .arg(propertyMirror.getSafeName());
    }
}
