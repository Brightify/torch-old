package org.brightify.torch.compile.marshall;

import com.google.inject.Inject;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import org.brightify.torch.Ref;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityDescriptionGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.compile.util.TypeHelper;

import javax.annotation.processing.Messager;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ReferenceMarshaller extends AbstractMarshaller {

    @Inject
    private EntityContext entityContext;

    @Inject
    private Messager messager;

    @Inject
    private Types types;

    @Inject
    private TypeHelper typeHelper;

    public ReferenceMarshaller() {
        super(Ref.class);
    }

    @Override
    public boolean accepts(TypeMirror type) {
        DeclaredType ref = types.getDeclaredType(typeHelper.elementOf(Ref.class), types.getWildcardType(null, null));

        if (!types.isAssignable(typeHelper.getWrappedType(type), ref)) {
            return false;
        }

        TypeMirror refType = typeHelper.singleGenericParameter(type);

        return entityContext.containsEntity(refType.toString());
    }

    @Override
    protected JClass getBackingType(PropertyMirror propertyMirror) {
        return CodeModelTypes.REF
                .narrow(CodeModelTypes.ref(typeHelper.singleGenericParameter(propertyMirror.getType()).toString()));
    }

    @Override
    public PropertyType getPropertyType() {
        return PropertyType.REFERENCE;
    }

    @Override
    protected JExpression fromRawEntity(EntityDescriptionGenerator.CreateFromRawEntityHolder holder,
                                        PropertyMirror propertyMirror) {
        JExpression getEntityId = holder.rawEntity.invoke("getLong").arg(JExpr.lit(propertyMirror.getSafeName()));

        JFieldRef propertyDescription = holder.classHolder.definedClass.staticRef(propertyMirror.getName());

        return holder.loadContainer.invoke("requestReference").arg(propertyDescription).arg(getEntityId);
    }

    @Override
    protected boolean nullable(PropertyMirror propertyMirror) {
        return true;
    }

    @Override
    protected JClass propertyClass(PropertyMirror propertyMirror) {
        return CodeModelTypes.REFERENCE_PROPERTY
                .narrow(CodeModelTypes.ref(propertyMirror.getOwner()))
                .narrow(CodeModelTypes.ref(typeHelper.singleGenericParameter(typeHelper.getWrappedType(propertyMirror))
                                                     .toString()));
    }

    @Override
    protected JClass propertyClassBase(PropertyMirror propertyMirror) {
        return CodeModelTypes.REFERENCE_PROPERTY_IMPL
                .narrow(CodeModelTypes.ref(propertyMirror.getOwner()))
                .narrow(CodeModelTypes.ref(typeHelper.singleGenericParameter(typeHelper.getWrappedType(propertyMirror))
                                                     .toString()));
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
