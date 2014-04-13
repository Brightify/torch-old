package org.brightify.torch.compile.generate;

import com.google.inject.Inject;
import com.sun.codemodel.JArray;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import org.brightify.torch.Torch;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.EntityMirror;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.marshall.Marshaller;
import org.brightify.torch.compile.marshall.MarshallerRegistry;
import org.brightify.torch.compile.migration.MigrationPath;
import org.brightify.torch.compile.migration.MigrationPathPart;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.compile.util.TypeHelper;
import org.brightify.torch.sql.ColumnDef;
import org.brightify.torch.sql.constraint.ColumnConstraint;
import org.brightify.torch.sql.statement.CreateTable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityMetadataGeneratorImpl implements EntityMetadataGenerator {

    public static final String METADATA_POSTFIX = "$";

    @Inject
    private EntityContext entityContext;

    @Inject
    private MarshallerRegistry marshallerRegistry;

    @Inject
    private TypeHelper typeHelper;

    @Override
    public void generate(EntityMirror entityMirror) throws Exception {
        JCodeModel codeModel = CodeModelTypes.CODE_MODEL;

        JDefinedClass definedClass =
                codeModel._class(entityMirror.getFullName() + EntityMetadataGeneratorImpl.METADATA_POSTFIX);

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

        for (Property property : entityMirror.getProperties()) {
            Marshaller marshaller = marshallerRegistry.getMarshallerOrThrow(property);

            marshaller.createColumnField(definedClass, property);
        }

        generate_createTable(classHolder);
        generate_createFromCursor(classHolder);
        generate_toContentValues(classHolder);
        generate_migrate(classHolder);
        generate_utilityMethods(classHolder);
    }

    private void generate_createTable(ClassHolder classHolder) {
        JMethod createTableMethod = classHolder.definedClass.method(JMod.PUBLIC, Void.TYPE, "createTable");
        createTableMethod.annotate(Override.class);
        JVar createTableTorchParameter = createTableMethod.param(Torch.class, "torch");

        StringBuilder sqlBuilder = new StringBuilder();

        for (CreateTable createTable : getCreateTables(classHolder.entityMirror)) {
            sqlBuilder.append(createTable.toSQLString()).append(";");
        }

        createTableMethod.body().add(
                JExpr.invoke(createTableTorchParameter, "getDatabase")
                     .invoke("execSQL")
                     .arg(JExpr.lit(sqlBuilder.toString()))
        );

    }

    private List<CreateTable> getCreateTables(EntityMirror entityMirror) {
        List<CreateTable> tablesToCreate = new ArrayList<CreateTable>();
        CreateTable createTable = new CreateTable();
        createTable.setTableName(entityMirror.getTableName());
        for (Property property : entityMirror.getProperties()) {

            Marshaller marshaller = marshallerRegistry.getMarshallerOrThrow(property);

            ColumnDef columnDef = marshaller.createColumn(tablesToCreate, property);
            if(columnDef == null) {
                continue;
            }
            if (property.getId() != null) {
                ColumnConstraint.PrimaryKey primaryKey = new ColumnConstraint.PrimaryKey();
                primaryKey.setAutoIncrement(property.getId().autoIncrement());
                columnDef.addColumnConstraint(primaryKey);
            }
            if (property.getNotNull() != null) {
                ColumnConstraint.NotNull notNull = new ColumnConstraint.NotNull();
                columnDef.addColumnConstraint(notNull);
            }
            if (property.getUnique() != null) {
                ColumnConstraint.Unique unique = new ColumnConstraint.Unique();
                columnDef.addColumnConstraint(unique);
            }
            createTable.addColumnDef(columnDef);
        }

        tablesToCreate.add(createTable);

        return tablesToCreate;
    }

    private void generate_createFromCursor(ClassHolder classHolder) {
        CreateFromCursorHolder holder = new CreateFromCursorHolder();
        holder.classHolder = classHolder;
        holder.method = classHolder.definedClass.method(JMod.PUBLIC, classHolder.entityClass, "createFromCursor");
        holder.method.annotate(Override.class);

        holder.torch = holder.method.param(CodeModelTypes.TORCH, "torch");
        holder.cursor = holder.method.param(CodeModelTypes.CURSOR, "cursor");
        holder.loadGroups =
                holder.method.param(CodeModelTypes.LIST.narrow(CodeModelTypes.CLASS.narrow(
                        CodeModelTypes.OBJECT.wildcard())), "loadGroups");

        holder.entity = holder.method
                .body()
                .decl(classHolder.entityClass, "entity", JExpr._new(classHolder.entityClass));

        for (Property property : classHolder.entityMirror.getProperties()) {
            Marshaller marshaller = marshallerRegistry.getMarshallerOrThrow(property);

            holder.method.body().directStatement("// " + property.getType() + " by " + marshaller.getClass().getName());
            holder.method.body().add(marshaller.unmarshall(holder, property));
        }
        holder.method.body()._return(holder.entity);
    }

    private void generate_toContentValues(ClassHolder classHolder) {
        ToContentValuesHolder holder = new ToContentValuesHolder();
        holder.classHolder = classHolder;
        holder.method = classHolder.definedClass.method(JMod.PUBLIC, CodeModelTypes.CONTENT_VALUES, "toContentValues");
        holder.method.annotate(Override.class);

        holder.torch = holder.method.param(CodeModelTypes.TORCH, "torch");
        holder.entity = holder.method.param(classHolder.entityClass, "entity");

        holder.contentValues = holder.method
                .body()
                .decl(CodeModelTypes.CONTENT_VALUES, "contentValues", JExpr._new(CodeModelTypes.CONTENT_VALUES));

        for (Property property : classHolder.entityMirror.getProperties()) {
            Marshaller marshaller = marshallerRegistry.getMarshallerOrThrow(property);

            holder.method.body().directStatement("// " + property.getType() + " by " + marshaller.getClass().getName());
            holder.method.body().add(marshaller.marshall(holder, property));
        }
        holder.method.body()._return(holder.contentValues);
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
                conditional = conditional._elseif(ifExpression);
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
            conditional._then()._throw(exception);
        } else {
            method.body()._throw(exception);
        }
    }

    private void generate_utilityMethods(ClassHolder classHolder) {


        classHolder.definedClass.method(JMod.PUBLIC,
                                        CodeModelTypes.NUMBER_PROPERTY.narrow(CodeModelTypes.LONG), "getIdColumn")
                                .body()._return(JExpr.refthis(classHolder.entityMirror.getIdProperty().getName()));


        JArray getColumns_columns = JExpr.newArray(CodeModelTypes.STRING);
        for (Property property : classHolder.entityMirror.getProperties()) {
            getColumns_columns.add(JExpr.lit(property.getColumnName()));
        }
        classHolder.definedClass.method(JMod.PUBLIC, CodeModelTypes.STRING.array(), "getColumns")
                                .body()._return(getColumns_columns);


        classHolder.definedClass.method(JMod.PUBLIC, CodeModelTypes.STRING, "getTableName")
                                .body()._return(JExpr.lit(classHolder.entityMirror.getTableName()));


        classHolder.definedClass.method(JMod.PUBLIC, CodeModelTypes.STRING, "getVersion")
                                .body()._return(JExpr.lit(classHolder.entityMirror.getVersion()));


        classHolder.definedClass.method(JMod.PUBLIC, CodeModelTypes.ENTITY_MIGRATION_TYPE, "getMigrationType")
                                .body()._return(
                CodeModelTypes.ENTITY_MIGRATION_TYPE.staticRef(classHolder.entityMirror.getMigrationType().toString()));


        JMethod getEntityId = classHolder.definedClass.method(JMod.PUBLIC, CodeModelTypes.LONG, "getEntityId");
        JVar getEntityId_Entity = getEntityId.param(classHolder.entityClass, "entity");
        getEntityId.body()._return(classHolder.entityMirror.getIdProperty().getGetter().getValue(getEntityId_Entity));


        JMethod setEntityId = classHolder.definedClass.method(JMod.PUBLIC, Void.TYPE, "setEntityId");
        JVar setEntityId_Entity = setEntityId.param(classHolder.entityClass, "entity");
        JVar setEntityId_Id = setEntityId.param(CodeModelTypes.LONG, "id");
        setEntityId.body().add(
                classHolder.entityMirror.getIdProperty().getSetter().setValue(setEntityId_Entity, setEntityId_Id));


        JMethod getEntityClass = classHolder.definedClass.method(JMod.PUBLIC,
                                                                 CodeModelTypes.CLASS.narrow(classHolder.entityClass),
                                                                 "getEntityClass");
        getEntityClass.body()._return(classHolder.entityClass.dotclass());


        JMethod createKey = classHolder.definedClass.method(JMod.PUBLIC,
                                                            CodeModelTypes.KEY.narrow(classHolder.entityClass),
                                                            "createKey");
        JVar createKey_Entity = createKey.param(classHolder.entityClass, "entity");
        createKey.body()._return(
                CodeModelTypes.KEY_FACTORY
                        .staticInvoke("create")
                        .arg(JExpr.invoke(getEntityClass))
                        .arg(JExpr.invoke(getEntityId).arg(createKey_Entity))
        );


        classHolder.definedClass.method(JMod.PUBLIC | JMod.STATIC, classHolder.definedClass, "create")
                                .body()._return(JExpr._new(classHolder.definedClass));
    }


}
