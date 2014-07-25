package org.brightify.torch.compile.marshall;

import com.google.inject.Inject;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.feature.FeatureProviderRegistry;
import org.brightify.torch.compile.generate.EntityDescriptionGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.compile.util.TypeHelper;
import org.brightify.torch.sql.ColumnDef;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.statement.CreateTable;

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
    public int getPriority() {
        return 0;
    }

    @Override
    public JStatement marshall(EntityDescriptionGenerator.ToRawEntityHolder holder, PropertyMirror propertyMirror) {
        return holder.rawEntity
                .invoke("put").arg(JExpr.lit(propertyMirror.getSafeName())).arg(marshallValue(holder, propertyMirror));
    }

    @Override
    public JStatement unmarshall(EntityDescriptionGenerator.CreateFromRawEntityHolder holder, PropertyMirror propertyMirror) {
        //JBlock block= new JBlock();
        //JVar index = block.decl(CodeModelTypes.INTEGER, "index", getIndex(holder.rawEntity, propertyMirror));
        //JConditional isNull = block._if(index.ne(JExpr._null()));

        return propertyMirror.getSetter().setValue(holder.entity, fromRawEntity(holder, propertyMirror));

        //isNull._then().add(propertyMirror.getSetter().setValue(holder.entity, fromRawEntity(holder, index, propertyMirror)));
        //if(nullable(propertyMirror)) {
        //    isNull._else().add(propertyMirror.getSetter().setValue(holder.entity, JExpr._null()));
        //}
        //return block;
    }

    @Override
    public ColumnDef createColumn(EntityDescriptionGenerator.ClassHolder holder, List<CreateTable> tablesToCreate, PropertyMirror propertyMirror) {
        ColumnDef columnDef = new ColumnDef(propertyMirror.getName());
        columnDef.setTypeAffinity(getAffinity());
        return columnDef;
    }

    @Override
    public JFieldVar createPropertyField(EntityDescriptionGenerator.ClassHolder holder, PropertyMirror propertyMirror) {
        return holder.definedClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
                                         propertyClass(propertyMirror),
                                         propertyMirror.getName(),
                                         propertyClassInvocation(propertyMirror));
    }

    protected JExpression marshallValue(EntityDescriptionGenerator.ToRawEntityHolder holder, PropertyMirror propertyMirror) {
        return propertyMirror.getGetter().getValue(holder.entity);
    }

    protected JInvocation propertyClassInvocation(PropertyMirror propertyMirror) {

        JInvocation propertyInvocation = JExpr._new(propertyClassImpl(propertyMirror))
                                              .arg(propertyMirror.getName())
                                              .arg(propertyMirror.getSafeName())
                                              .arg(CodeModelTypes.ref(getBackingType(propertyMirror)).dotclass());

        return propertyClassInvocationFeatureParameters(propertyInvocation, propertyMirror);
    }

    protected JInvocation propertyClassInvocationFeatureParameters(JInvocation propertyInvocation,
                                                                   PropertyMirror propertyMirror) {
        List<JExpression> featureConstructions = featureProviderRegistry.getFeatureConstructions(propertyMirror);

        for (JExpression featureConstruction : featureConstructions) {
            propertyInvocation = propertyInvocation.arg(featureConstruction);
        }

        return propertyInvocation;
    }

    protected Class<?> getBackingType(PropertyMirror propertyMirror) {
        return acceptedClass;
    }

    protected abstract JExpression fromRawEntity(EntityDescriptionGenerator.CreateFromRawEntityHolder holder,
                                                 PropertyMirror propertyMirror);

    protected abstract boolean nullable(PropertyMirror propertyMirror);

    protected abstract TypeAffinity getAffinity();

    protected abstract JClass propertyClass(PropertyMirror propertyMirror);

    protected abstract JClass propertyClassImpl(PropertyMirror propertyMirror);

    public static JExpression getIndex(JVar cursor, PropertyMirror propertyMirror) {
        return cursor.invoke("getColumnIndexOrThrow").arg(JExpr.lit(propertyMirror.getSafeName()));
    }


}
