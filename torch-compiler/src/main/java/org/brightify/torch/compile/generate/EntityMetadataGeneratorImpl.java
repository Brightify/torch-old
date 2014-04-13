package org.brightify.torch.compile.generate;

import com.google.inject.Inject;
import com.sun.codemodel.JArray;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.Torch;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.EntityInfo;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.migration.MigrationPath;
import org.brightify.torch.compile.migration.MigrationPathPart;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.compile.filter.ColumnProvider;
import org.brightify.torch.compile.filter.ColumnRegistry;
import org.brightify.torch.generate.MetadataSourceFileImpl;
import org.brightify.torch.marshall2.Marshaller;
import org.brightify.torch.marshall2.MarshallerProvider;
import org.brightify.torch.sql.ColumnDef;
import org.brightify.torch.sql.constraint.ColumnConstraint;
import org.brightify.torch.sql.statement.CreateTable;
import org.brightify.torch.util.TypeHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityMetadataGeneratorImpl implements EntityMetadataGenerator {

    @Inject
    private EntityContext entityContext;

    @Inject
    private MarshallerProvider marshallerProvider;

    @Inject
    private TypeHelper typeHelper;

    @Inject
    private ColumnRegistry columnRegistry;

    @Override
    public JCodeModel generate(EntityInfo entityInfo) throws Exception {
        JCodeModel codeModel = CodeModelTypes.getCodeModel();

        JDefinedClass definedClass =
                codeModel._class(entityInfo.getFullName() + MetadataSourceFileImpl.METADATA_POSTFIX);
        Holder holder = new Holder(entityInfo, definedClass);


        definedClass.constructor(JMod.PRIVATE);
        definedClass._implements(codeModel.ref(EntityMetadata.class).narrow(holder.entityClass));

        for (Property property : entityInfo.getProperties()) {
            ColumnProvider columnProvider = columnRegistry.getColumnProvider(property);

            columnProvider.createColumnField(definedClass, property);
        }

        generate_createTable(holder);
        generate_createFromCursor(holder);
        generate_toContentValues(holder);
        generate_migrate(holder);
        generate_utilityMethods(holder);

        return codeModel;
    }

    private List<CreateTable> getCreateTables(EntityInfo entityInfo) {
        List<CreateTable> createTables = new ArrayList<CreateTable>();
        CreateTable createTable = new CreateTable();
        createTable.setTableName(entityInfo.getTableName());
        for (Property property : entityInfo.getProperties()) {

            Marshaller marshaller = marshallerProvider.getMarshallerOrThrow(property);

            ColumnDef columnDef = new ColumnDef(property.getColumnName());
            columnDef.setTypeAffinity(marshaller.getAffinity());
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

        createTables.add(createTable);

        return createTables;
    }

    private void generate_createTable(Holder holder) {
        JMethod createTableMethod = holder.definedClass.method(JMod.PUBLIC, Void.TYPE, "createTable");
        createTableMethod.annotate(Override.class);
        JVar createTableTorchParameter = createTableMethod.param(Torch.class, "torch");

        StringBuilder sqlBuilder = new StringBuilder();

        for (CreateTable createTable : getCreateTables(holder.entityInfo)) {
            sqlBuilder.append(createTable.toSQLString()).append(";");
        }

        createTableMethod.body().add(
                JExpr.invoke(createTableTorchParameter, "getDatabase")
                     .invoke("execSQL")
                     .arg(JExpr.lit(sqlBuilder.toString()))
        );

    }

    private void generate_createFromCursor(Holder holder) {
        JMethod method = holder.definedClass.method(JMod.PUBLIC, holder.entityClass, "createFromCursor");
        method.annotate(Override.class);

        JVar torch = method.param(CodeModelTypes.TORCH, "torch");
        JVar cursor = method.param(CodeModelTypes.CURSOR, "cursor");

        JVar entity = method.body().decl(holder.entityClass, "entity", JExpr._new(holder.entityClass));

        for (Property property : holder.entityInfo.getProperties()) {
            Marshaller marshaller = marshallerProvider.getMarshallerOrThrow(property);

            method.body().directStatement("// " + property.getType() + " by " + marshaller.getClass().getName());
            method.body().add(marshaller.unmarshall(torch, cursor, entity, property));
        }
        method.body()._return(entity);
    }

    private void generate_toContentValues(Holder holder) {
        JMethod method = holder.definedClass.method(JMod.PUBLIC, CodeModelTypes.CONTENT_VALUES, "toContentValues");
        method.annotate(Override.class);

        JVar torch = method.param(CodeModelTypes.TORCH, "torch");
        JVar entity = method.param(holder.entityClass, "entity");

        JVar contentValues = method
                .body()
                .decl(CodeModelTypes.CONTENT_VALUES, "contentValues", JExpr._new(CodeModelTypes.CONTENT_VALUES));

        for (Property property : holder.entityInfo.getProperties()) {
            Marshaller marshaller = marshallerProvider.getMarshallerOrThrow(property);


            method.body().directStatement("// " + property.getType() + " by " + marshaller.getClass().getName());
            method.body().add(marshaller.marshall(torch, contentValues, entity, property));
        }
        method.body()._return(contentValues);
    }

    private void generate_migrate(Holder holder) {
        JMethod method = holder.definedClass.method(JMod.PUBLIC, Void.TYPE, "migrate");
        method.annotate(Override.class);

        JVar assistant = method.param(CodeModelTypes.MIGRATION_ASSISTANT.narrow(holder.entityClass), "assistant");
        JVar sourceVersion = method.param(CodeModelTypes.STRING, "sourceVersion");
        JVar targetVersion = method.param(CodeModelTypes.STRING, "targetVersion");

        JVar migration = method.body().decl(CodeModelTypes.STRING, "migration",
                                            sourceVersion.plus(JExpr.lit("->")).plus(targetVersion));
        JConditional conditional = null;
        for (MigrationPath migrationPath : holder.entityInfo.getMigrationPaths()) {
            JExpression ifExpression = migration.invoke("equals").arg(JExpr.lit(migrationPath.getDescription()));
            if (conditional == null) {
                conditional = method.body()._if(ifExpression);
            } else {
                conditional = conditional._elseif(ifExpression);
            }
            for (MigrationPathPart part = migrationPath.getStart(); part != null; part = part.getNext()) {
                String migrationMethodName = part.getMigrationMethod().getExecutable().getSimpleName().toString();
                conditional._then().add(holder.entityClass.staticInvoke(migrationMethodName).arg(assistant));
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

    private void generate_utilityMethods(Holder holder) {



        holder.definedClass.method(JMod.PUBLIC,
                                   CodeModelTypes.NUMBER_COLUMN.narrow(CodeModelTypes.LONG), "getIdColumn")
                           .body()._return(JExpr.refthis(holder.entityInfo.getIdProperty().getName()));


        JArray getColumns_columns = JExpr.newArray(CodeModelTypes.STRING);
        for (Property property : holder.entityInfo.getProperties()) {
            getColumns_columns.add(JExpr.lit(property.getColumnName()));
        }
        holder.definedClass.method(JMod.PUBLIC, CodeModelTypes.STRING.array(), "getColumns")
                           .body()._return(getColumns_columns);


        holder.definedClass.method(JMod.PUBLIC, CodeModelTypes.STRING, "getTableName")
                           .body()._return(JExpr.lit(holder.entityInfo.getTableName()));


        holder.definedClass.method(JMod.PUBLIC, CodeModelTypes.STRING, "getVersion")
                           .body()._return(JExpr.lit(holder.entityInfo.getVersion()));


        holder.definedClass.method(JMod.PUBLIC, CodeModelTypes.ENTITY_MIGRATION_TYPE, "getMigrationType")
                           .body()._return(
                CodeModelTypes.ENTITY_MIGRATION_TYPE.staticRef(holder.entityInfo.getMigrationType().toString()));


        JMethod getEntityId = holder.definedClass.method(JMod.PUBLIC, CodeModelTypes.LONG, "getEntityId");
        JVar getEntityId_Entity = getEntityId.param(holder.entityClass, "entity");
        getEntityId.body()._return(holder.entityInfo.getIdProperty().getGetter().getValue(getEntityId_Entity));


        JMethod setEntityId = holder.definedClass.method(JMod.PUBLIC, Void.TYPE, "setEntityId");
        JVar setEntityId_Entity = setEntityId.param(holder.entityClass, "entity");
        JVar setEntityId_Id = setEntityId.param(CodeModelTypes.LONG, "id");
        setEntityId.body().add(
                holder.entityInfo.getIdProperty().getSetter().setValue(setEntityId_Entity, setEntityId_Id));


        JMethod getEntityClass = holder.definedClass.method(JMod.PUBLIC,
                                                            CodeModelTypes.CLASS.narrow(holder.entityClass),
                                                            "getEntityClass");
        getEntityClass.body()._return(holder.entityClass.dotclass());


        JMethod createKey = holder.definedClass.method(JMod.PUBLIC,
                                                       CodeModelTypes.KEY.narrow(holder.entityClass),
                                                       "createKey");
        JVar createKey_Entity = createKey.param(holder.entityClass, "entity");
        createKey.body()._return(
                CodeModelTypes.KEY_FACTORY
                        .staticInvoke("create")
                        .arg(JExpr.invoke(getEntityClass))
                        .arg(JExpr.invoke(getEntityId).arg(createKey_Entity))
        );


        holder.definedClass.method(JMod.PUBLIC | JMod.STATIC, holder.definedClass, "create")
                           .body()._return(JExpr._new(holder.definedClass));
    }

    private static class Holder {

        private final EntityInfo entityInfo;
        private final JClass entityClass;
        private final JDefinedClass definedClass;

        Holder(EntityInfo entityInfo, JDefinedClass definedClass) {
            this.entityInfo = entityInfo;
            this.entityClass = definedClass.owner().ref(entityInfo.getFullName());
            this.definedClass = definedClass;
        }
    }
}
