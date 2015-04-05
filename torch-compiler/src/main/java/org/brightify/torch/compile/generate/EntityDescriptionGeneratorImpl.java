package org.brightify.torch.compile.generate;

import com.google.inject.Inject;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.EntityMirror;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.marshall.Marshaller;
import org.brightify.torch.compile.marshall.MarshallerRegistry;
import org.brightify.torch.compile.migration.MigrationPath;
import org.brightify.torch.compile.migration.MigrationPathPart;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.compile.util.TypeHelper;
import org.brightify.torch.util.Constants;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityDescriptionGeneratorImpl implements EntityDescriptionGenerator {

    @Inject
    private EntityContext entityContext;

    @Inject
    private MarshallerRegistry marshallerRegistry;

    @Inject
    private TypeHelper typeHelper;

    @Override
    public void generate(EntityMirror entityMirror) throws Exception {
        JCodeModel codeModel = CodeModelTypes.CODE_MODEL;

        JDefinedClass definedClass = codeModel._class(entityMirror.getFullName() + Constants.DESCRIPTION_POSTFIX);

        generate(entityMirror, definedClass);
    }

    @Override
    public void generate(EntityMirror entityMirror, JDefinedClass definedClass) throws Exception {
        ClassHolder classHolder = new ClassHolder();
        classHolder.definedClass = definedClass;
        classHolder.entityMirror = entityMirror;
        classHolder.entityClass = CodeModelTypes.ref(entityMirror.getFullName());


        definedClass.constructor(JMod.PRIVATE);
        definedClass._implements(CodeModelTypes.ENTITY_METADATA.narrow(classHolder.entityClass));

        JInvocation propertiesInvocation = JExpr._new(CodeModelTypes.ARRAY_LIST_BUILDER
                .narrow(CodeModelTypes.PROPERTY.narrow(classHolder.entityClass).narrow(CodeModelTypes.WILDCARD)));

        JInvocation valuePropertiesInvocation = JExpr._new(CodeModelTypes.ARRAY_LIST_BUILDER
                .narrow(CodeModelTypes.VALUE_PROPERTY.narrow(classHolder.entityClass).narrow(CodeModelTypes.WILDCARD)));

        JInvocation referencePropertiesInvocation = JExpr._new(CodeModelTypes.ARRAY_LIST_BUILDER
                .narrow(CodeModelTypes.REFERENCE_PROPERTY.narrow(classHolder.entityClass).narrow(CodeModelTypes.WILDCARD)));

        /*JArray propertiesArray = JExpr.newArray(
                CodeModelTypes.PROPERTY.narrow(classHolder.entityClass).narrow(CodeModelTypes.WILDCARD));
        JArray valuePropertiesArray = JExpr.newArray(
                CodeModelTypes.PROPERTY.narrow(classHolder.entityClass).narrow(CodeModelTypes.WILDCARD));
        JArray referencePropertiesArray = JExpr.newArray(
                CodeModelTypes.REFERENCE_PROPERTY.narrow(classHolder.entityClass).narrow(CodeModelTypes.WILDCARD));*/
        for (PropertyMirror propertyMirror : entityMirror.getProperties()) {
            Marshaller marshaller = marshallerRegistry.getMarshallerOrThrow(propertyMirror);

            JFieldVar propertyField = marshaller.createPropertyField(classHolder, propertyMirror);

            propertiesInvocation = propertiesInvocation.invoke("add").arg(propertyField);

            switch (marshaller.getPropertyType()) {
                case VALUE:
                    valuePropertiesInvocation = valuePropertiesInvocation.invoke("add").arg(propertyField);
                    break;
                case REFERENCE:
                    referencePropertiesInvocation = referencePropertiesInvocation.invoke("add").arg(propertyField);
                    break;
            }
        }

        JFieldVar propertiesField = classHolder.definedClass.field(
                JMod.PRIVATE | JMod.STATIC | JMod.FINAL,
                CodeModelTypes.LIST.narrow(
                        CodeModelTypes.PROPERTY
                                .narrow(classHolder.entityClass)
                                .narrow(CodeModelTypes.WILDCARD)
                                .wildcard()),
                "properties",
                propertiesInvocation.invoke("list"));

        JFieldVar valuePropertiesField = classHolder.definedClass.field(
                JMod.PRIVATE | JMod.STATIC | JMod.FINAL,
                CodeModelTypes.LIST.narrow(
                        CodeModelTypes.VALUE_PROPERTY
                                .narrow(classHolder.entityClass)
                                .narrow(CodeModelTypes.WILDCARD)
                                .wildcard()),
                "valueProperties",
                valuePropertiesInvocation.invoke("list"));

        JFieldVar referencePropertiesField = classHolder.definedClass.field(
                JMod.PRIVATE | JMod.STATIC | JMod.FINAL,
                CodeModelTypes.LIST.narrow(
                        CodeModelTypes.REFERENCE_PROPERTY
                                .narrow(classHolder.entityClass)
                                .narrow(CodeModelTypes.WILDCARD)
                                .wildcard()),
                "referenceProperties",
                referencePropertiesInvocation.invoke("list"));

        JMethod getPropertiesMethod = classHolder.definedClass.method(
                JMod.PUBLIC,
                CodeModelTypes.LIST.narrow(
                        CodeModelTypes.PROPERTY
                                .narrow(classHolder.entityClass)
                                .narrow(CodeModelTypes.WILDCARD)
                                .wildcard()),
                "getProperties");
        getPropertiesMethod.annotate(Override.class);
        getPropertiesMethod.body()._return(propertiesField);

        JMethod getValuePropertiesMethod = classHolder.definedClass.method(
                JMod.PUBLIC,
                CodeModelTypes.LIST.narrow(
                        CodeModelTypes.VALUE_PROPERTY
                                .narrow(classHolder.entityClass)
                                .narrow(CodeModelTypes.WILDCARD)
                                .wildcard()),
                "getValueProperties");
        getValuePropertiesMethod.annotate(Override.class);
        getValuePropertiesMethod.body()._return(valuePropertiesField);

        JMethod getReferencePropertiesMethod = classHolder.definedClass.method(
                JMod.PUBLIC,
                CodeModelTypes.LIST.narrow(
                        CodeModelTypes.REFERENCE_PROPERTY
                                .narrow(classHolder.entityClass)
                                .narrow(CodeModelTypes.WILDCARD)
                                .wildcard()),
                "getReferenceProperties");
        getReferencePropertiesMethod.annotate(Override.class);
        getReferencePropertiesMethod.body()._return(referencePropertiesField);


        generate_migrate(classHolder);
        generate_utilityMethods(classHolder);
    }

    private void generate_migrate(ClassHolder classHolder) {
        JMethod method = classHolder.definedClass.method(JMod.PUBLIC, Void.TYPE, "migrate");
        method.annotate(Override.class);

        JVar assistant = method.param(CodeModelTypes.MIGRATION_ASSISTANT.narrow(classHolder.entityClass), "assistant");
        JVar sourceRevision = method.param(CodeModelTypes.LONG_PRIMITIVE, "sourceRevision");
        JVar targetRevision = method.param(CodeModelTypes.LONG_PRIMITIVE, "targetRevision");

        JVar migration = method.body().decl(CodeModelTypes.STRING, "migration",
                                            sourceRevision.plus(JExpr.lit("->")).plus(targetRevision));
        JConditional conditional = null;
        for (MigrationPath migrationPath : classHolder.entityMirror.getMigrationPaths()) {
            JExpression ifExpression = migration.invoke("equals").arg(JExpr.lit(migrationPath.getDescription()));
            if (conditional == null) {
                conditional = method.body()._if(ifExpression);
            } else {
                // We don't want the unnecessary indentation and braces. If it does not work though, we fallback to
                // default JCodeModel behavior.
                try {
                    JBlock elseBlock = conditional._else();
                    Field bracesRequired = JBlock.class.getDeclaredField("bracesRequired");
                    Field indentRequired = JBlock.class.getDeclaredField("indentRequired");
                    bracesRequired.setAccessible(true);
                    bracesRequired.setBoolean(elseBlock, false);
                    indentRequired.setAccessible(true);
                    indentRequired.setBoolean(elseBlock, false);
                    conditional = elseBlock._if(ifExpression);
                } catch (Exception e) {
                    conditional = conditional._elseif(ifExpression);
                }
            }
            for (MigrationPathPart part = migrationPath.getStart(); part != null; part = part.getNext()) {
                String migrationMethodName = part.getMigrationMethod().getExecutable().getSimpleName().toString();
                conditional._then().add(classHolder.entityClass.staticInvoke(migrationMethodName).arg(assistant));
            }
        }

        JExpression exception = JExpr._new(CodeModelTypes.MIGRATION_EXCEPTION).arg(
                JExpr.lit("Unable to migrate entity! Could not find migration path from '")
                     .plus(sourceRevision)
                     .plus(JExpr.lit("' to '"))
                     .plus(targetRevision)
                     .plus(JExpr.lit("'!"))
        );

        if (conditional != null) {
            conditional._else()._throw(exception);
        } else {
            method.body()._throw(exception);
        }
    }

    private void generate_utilityMethods(ClassHolder classHolder) {
        JMethod getIdProperty = classHolder.definedClass.method(JMod.PUBLIC,
                                                                CodeModelTypes.NUMBER_PROPERTY
                                                                        .narrow(classHolder.entityClass)
                                                                        .narrow(CodeModelTypes.LONG), "getIdProperty");
        getIdProperty.annotate(Override.class);
        getIdProperty.body()._return(JExpr.refthis(classHolder.entityMirror.getIdPropertyMirror().getName()));

        JMethod getSafeName = classHolder.definedClass.method(JMod.PUBLIC, CodeModelTypes.STRING, "getSafeName");
        getSafeName.annotate(Override.class);
        getSafeName.body()._return(JExpr.lit(classHolder.entityMirror.getSafeName()));


        JMethod getVersion = classHolder.definedClass.method(JMod.PUBLIC, CodeModelTypes.LONG_PRIMITIVE, "getRevision");
        getVersion.annotate(Override.class);
        getVersion.body()._return(JExpr.lit(classHolder.entityMirror.getRevision()));


        JMethod getMigrationType =
                classHolder.definedClass.method(JMod.PUBLIC, CodeModelTypes.ENTITY_MIGRATION_TYPE, "getMigrationType");
        getMigrationType.annotate(Override.class);
        getMigrationType.body()._return(
                CodeModelTypes.ENTITY_MIGRATION_TYPE.staticRef(classHolder.entityMirror.getMigrationType().toString()));


        JMethod getEntityClass = classHolder.definedClass.method(JMod.PUBLIC,
                                                                 CodeModelTypes.CLASS.narrow(classHolder.entityClass),
                                                                 "getEntityClass");
        getEntityClass.annotate(Override.class);
        getEntityClass.body()._return(classHolder.entityClass.dotclass());

        JMethod createEmpty = classHolder.definedClass.method(JMod.PUBLIC,
                                                              classHolder.entityClass,
                                                              "createEmpty");
        createEmpty.annotate(Override.class);
        createEmpty.body()._return(JExpr._new(classHolder.entityClass));


        classHolder.definedClass.method(JMod.PUBLIC | JMod.STATIC, classHolder.definedClass, "create")
                                .body()._return(JExpr._new(classHolder.definedClass));
    }


}
