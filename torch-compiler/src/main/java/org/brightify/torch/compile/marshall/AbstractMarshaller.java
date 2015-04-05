package org.brightify.torch.compile.marshall;

import com.google.inject.Inject;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.feature.FeatureProviderRegistry;
import org.brightify.torch.compile.generate.EntityDescriptionGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;
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

    @Inject
    private FeatureProviderRegistry featureProviderRegistry;

    public AbstractMarshaller(Class<?> acceptedClass) {
        this.acceptedClass = acceptedClass;
    }

    @Override
    public boolean accepts(TypeMirror type) {
        return types.isSameType(typeHelper.typeOf(acceptedClass), typeHelper.getWrappedType(type));
    }

    @Override
    public PropertyType getPropertyType() {
        return PropertyType.VALUE;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public JStatement marshall(EntityDescriptionGenerator.ToRawEntityHolder holder, PropertyMirror propertyMirror) {
        return holder.rawEntity
                .invoke("put").arg(JExpr.lit(propertyMirror.getSafeName())).arg(marshallValue(holder, propertyMirror));
    }

    @Override
    public JStatement unmarshall(EntityDescriptionGenerator.CreateFromRawEntityHolder holder,
                                 PropertyMirror propertyMirror) {
        //JBlock block= new JBlock();
        //JVar index = block.decl(CodeModelTypes.INTEGER, "index", getIndex(holder.rawEntity, propertyMirror));
        //JConditional isNull = block._if(index.ne(JExpr._null()));

        return propertyMirror.getSetter().setValue(holder.entity, fromRawEntity(holder, propertyMirror));

        //isNull._then().add(propertyMirror.getSetter().setValue(holder.entity, fromRawEntity(holder, index,
        // propertyMirror)));
        //if(nullable(propertyMirror)) {
        //    isNull._else().add(propertyMirror.getSetter().setValue(holder.entity, JExpr._null()));
        //}
        //return block;
    }

    @Override
    public JFieldVar createPropertyField(EntityDescriptionGenerator.ClassHolder holder, PropertyMirror propertyMirror)
            throws JClassAlreadyExistsException {
        return holder.definedClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
                                         propertyClass(propertyMirror),
                                         propertyMirror.getName(),
                                         propertyClassInvocation(holder, propertyMirror));
    }

    protected JExpression marshallValue(EntityDescriptionGenerator.ToRawEntityHolder holder,
                                        PropertyMirror propertyMirror) {
        return propertyMirror.getGetter().getValue(holder.entity);
    }

    protected JInvocation propertyClassInvocation(
            EntityDescriptionGenerator.ClassHolder holder, PropertyMirror propertyMirror)
            throws JClassAlreadyExistsException {
        JClass propertyInnerImpl = propertyInnerImpl(holder, propertyMirror);

        JInvocation propertyInvocation = JExpr._new(propertyInnerImpl);

        return propertyClassInvocationFeatureParameters(propertyInvocation, propertyMirror);

    }

    protected JInvocation propertyClassInvocationFeatureParameters(JInvocation propertyInvocation,
                                                                   PropertyMirror propertyMirror) {
        List<JExpression> featureConstructions = featureProviderRegistry.getFeatureConstructions(propertyMirror);

        for (JExpression featureConstruction : featureConstructions) {
            propertyInvocation = propertyInvocation.invoke("feature").arg(featureConstruction);
        }

        return propertyInvocation;
    }

    protected JClass getBackingType(PropertyMirror propertyMirror) {
        return CodeModelTypes.ref(acceptedClass);
    }

    protected abstract JExpression fromRawEntity(EntityDescriptionGenerator.CreateFromRawEntityHolder holder,
                                                 PropertyMirror propertyMirror);

    protected abstract boolean nullable(PropertyMirror propertyMirror);

    protected abstract JClass propertyClass(PropertyMirror propertyMirror);

    protected abstract JClass propertyClassBase(PropertyMirror propertyMirror);

    protected JClass propertyInnerImpl(EntityDescriptionGenerator.ClassHolder holder, PropertyMirror propertyMirror)
            throws JClassAlreadyExistsException {
        JClass baseClass = propertyClassBase(propertyMirror);

        JDefinedClass propertyImpl = holder.definedClass._class(
                JMod.PUBLIC | JMod.STATIC,
                "Property$" + holder.entityMirror.getSimpleName() + "$" + propertyMirror.getName() + "$" +
                baseClass.erasure().name());
        propertyImpl._extends(baseClass);

        JClass backingType = getBackingType(propertyMirror);

        JMethod constructor = propertyImpl.constructor(JMod.PUBLIC);
        invokeSuperConstructor(constructor.body(), holder.entityClass, propertyMirror);


        JMethod getMethod = propertyImpl.method(JMod.PUBLIC, backingType, "get");
        getMethod.annotate(Override.class);
        JVar ownerParam = getMethod.param(holder.entityClass, "owner");
        getMethod.body()._return(propertyMirror.getGetter().getValue(ownerParam));

        JMethod setMethod = propertyImpl.method(JMod.PUBLIC, CodeModelTypes.CODE_MODEL.VOID, "set");
        setMethod.annotate(Override.class);
        ownerParam = setMethod.param(holder.entityClass, "owner");
        JVar valueParam = setMethod.param(backingType, "value");
        setMethod.body().add(propertyMirror.getSetter().setValue(ownerParam, valueParam));

        JMethod defaultValue = propertyImpl.method(JMod.PUBLIC, propertyImpl, "defaultValue");
        defaultValue.annotate(Override.class);
        JVar defaultValueParam = defaultValue.param(backingType, "defaultValue");
        defaultValue.body().add(JExpr._super().invoke("defaultValue").arg(defaultValueParam))._return(JExpr._this());

        JMethod feature = propertyImpl.method(JMod.PUBLIC, propertyImpl, "feature");
        feature.annotate(Override.class);
        JVar featureParam = feature.param(CodeModelTypes.FEATURE, "feature");
        feature.body().add(JExpr._super().invoke("feature").arg(featureParam))._return(JExpr._this());

        return propertyImpl;
    }

    protected JInvocation invokeSuperConstructor(JBlock body, JClass entityClass, PropertyMirror propertyMirror) {
        return body.invoke("super").arg(entityClass.dotclass())
                   .arg(propertyMirror.getName())
                   .arg(propertyMirror.getSafeName());
    }

}
