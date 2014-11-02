package org.brightify.torch.compile.generate;

import com.google.inject.Inject;
import com.sun.codemodel.JArray;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
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

        JArray propertiesArray = JExpr.newArray(CodeModelTypes.PROPERTY.narrow(CodeModelTypes.WILDCARD));
        for (PropertyMirror propertyMirror : entityMirror.getProperties()) {
            Marshaller marshaller = marshallerRegistry.getMarshallerOrThrow(propertyMirror);

            JFieldVar propertyField = marshaller.createPropertyField(classHolder, propertyMirror);
            propertiesArray.add(propertyField);

        }
        JFieldVar propertiesField = classHolder.definedClass.field(JMod.PRIVATE | JMod.STATIC | JMod.FINAL,
                                       CodeModelTypes.PROPERTY.narrow(CodeModelTypes.WILDCARD).array(),
                                       "properties",
                                       propertiesArray);


        classHolder.definedClass.method(JMod.PUBLIC,
                                        CodeModelTypes.PROPERTY.narrow(CodeModelTypes.WILDCARD).array(),
                                        "getProperties")
                                .body()._return(propertiesField);


        generate_setFromRawEntity(classHolder);
        generate_toRawEntity(classHolder);
        generate_migrate(classHolder);
        generate_utilityMethods(classHolder);
    }

    private void generate_setFromRawEntity(ClassHolder classHolder) {
        CreateFromRawEntityHolder holder = new CreateFromRawEntityHolder();
        holder.classHolder = classHolder;
        holder.method = classHolder.definedClass.method(JMod.PUBLIC, Void.TYPE, "setFromRawEntity");
        holder.method.annotate(Override.class);

        holder.torchFactory = holder.method.param(CodeModelTypes.TORCH_FACTORY, "torchFactory");
        holder.rawEntity = holder.method.param(CodeModelTypes.READABLE_RAW_ENTITY, "rawEntity");
        holder.entity = holder.method.param(classHolder.entityClass, "entity");
        holder.loadGroups =
                holder.method.param(CodeModelTypes.SET.narrow(CodeModelTypes.CLASS.narrow(
                        CodeModelTypes.OBJECT.wildcard())), "loadGroups");

        for (PropertyMirror propertyMirror : classHolder.entityMirror.getProperties()) {
            Marshaller marshaller = marshallerRegistry.getMarshallerOrThrow(propertyMirror);

            holder.method.body().directStatement(
                    "// " + propertyMirror.getType() + " by " + marshaller.getClass().getName());
            holder.method.body().add(marshaller.unmarshall(holder, propertyMirror));
        }
    }

    private void generate_toRawEntity(ClassHolder classHolder) {
        ToRawEntityHolder holder = new ToRawEntityHolder();
        holder.classHolder = classHolder;
        holder.method = classHolder.definedClass.method(JMod.PUBLIC, Void.TYPE, "toRawEntity");
        holder.method.annotate(Override.class);

        holder.torchFactory = holder.method.param(CodeModelTypes.TORCH_FACTORY, "torchFactory");
        holder.entity = holder.method.param(classHolder.entityClass, "entity");
        holder.rawEntity = holder.method.param(CodeModelTypes.WRITABLE_RAW_ENTITY, "rawEntity");

        for (PropertyMirror propertyMirror : classHolder.entityMirror.getProperties()) {
            Marshaller marshaller = marshallerRegistry.getMarshallerOrThrow(propertyMirror);

            holder.method.body().directStatement(
                    "// " + propertyMirror.getType() + " by " + marshaller.getClass().getName());
            holder.method.body().add(marshaller.marshall(holder, propertyMirror));
        }
    }

    private void generate_migrate(ClassHolder classHolder) {
        JMethod method = classHolder.definedClass.method(JMod.PUBLIC, Void.TYPE, "migrate");
        method.annotate(Override.class);

        JVar assistant = method.param(CodeModelTypes.MIGRATION_ASSISTANT.narrow(classHolder.entityClass), "assistant");
        JVar sourceVersion = method.param(CodeModelTypes.STRING, "sourceVersion");
        JVar targetVersion = method.param(CodeModelTypes.STRING, "targetVersion");

        JVar migration = method.body().decl(CodeModelTypes.STRING, "migration",
                                            sourceVersion.plus(JExpr.lit("->")).plus(targetVersion));
        JConditional conditional = null;
        for (MigrationPath migrationPath : classHolder.entityMirror.getMigrationPaths()) {
            JExpression ifExpression = migration.invoke("equals").arg(JExpr.lit(migrationPath.getDescription()));
            if (conditional == null) {
                conditional = method.body()._if(ifExpression);
            } else {
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
                     .plus(sourceVersion)
                     .plus(JExpr.lit("' to '"))
                     .plus(targetVersion)
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
                                        CodeModelTypes.NUMBER_PROPERTY.narrow(CodeModelTypes.LONG), "getIdProperty");
        getIdProperty.annotate(Override.class);
        getIdProperty.body()._return(JExpr.refthis(classHolder.entityMirror.getIdPropertyMirror().getName()));

        JMethod getSafeName = classHolder.definedClass.method(JMod.PUBLIC, CodeModelTypes.STRING, "getSafeName");
        getSafeName.annotate(Override.class);
        getSafeName.body()._return(JExpr.lit(classHolder.entityMirror.getSafeName()));


        JMethod getVersion = classHolder.definedClass.method(JMod.PUBLIC, CodeModelTypes.STRING, "getVersion");
        getVersion.annotate(Override.class);
        getVersion.body()._return(JExpr.lit(classHolder.entityMirror.getVersion()));


        JMethod getMigrationType =
                classHolder.definedClass.method(JMod.PUBLIC, CodeModelTypes.ENTITY_MIGRATION_TYPE, "getMigrationType");
        getMigrationType.annotate(Override.class);
        getMigrationType.body()._return(
                CodeModelTypes.ENTITY_MIGRATION_TYPE.staticRef(classHolder.entityMirror.getMigrationType().toString()));


        JMethod getEntityId = classHolder.definedClass.method(JMod.PUBLIC, CodeModelTypes.LONG, "getEntityId");
        JVar getEntityId_Entity = getEntityId.param(classHolder.entityClass, "entity");
        getEntityId.annotate(Override.class);
        getEntityId.body()._return(classHolder.entityMirror.getIdPropertyMirror().getGetter().getValue(
                getEntityId_Entity));


        JMethod setEntityId = classHolder.definedClass.method(JMod.PUBLIC, Void.TYPE, "setEntityId");
        JVar setEntityId_Entity = setEntityId.param(classHolder.entityClass, "entity");
        JVar setEntityId_Id = setEntityId.param(CodeModelTypes.LONG, "id");
        setEntityId.annotate(Override.class);
        setEntityId.body().add(
                classHolder.entityMirror.getIdPropertyMirror().getSetter().setValue(setEntityId_Entity,
                                                                                    setEntityId_Id));


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
