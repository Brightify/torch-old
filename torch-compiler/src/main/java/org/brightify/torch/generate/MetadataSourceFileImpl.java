package org.brightify.torch.generate;

import com.google.inject.Inject;
import org.brightify.torch.marshall.Marshaller2;
import org.brightify.torch.marshall.MarshallerProvider2;
import org.brightify.torch.sql.ColumnDef;
import org.brightify.torch.sql.affinity.TextAffinity;
import org.brightify.torch.sql.constraint.ColumnConstraint;
import org.brightify.torch.sql.statement.CreateTable;
import org.brightify.torch.filter.ColumnInfo;
import org.brightify.torch.marshall.CursorMarshallerInfo;
import org.brightify.torch.parse.EntityInfo;
import org.brightify.torch.parse.MigrationPath;
import org.brightify.torch.parse.MigrationPathPart;
import org.brightify.torch.parse.Property;
import org.brightify.torch.util.TypeHelper;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class MetadataSourceFileImpl implements MetadataSourceFile {
    public static final String PRIVATE_FIELD_PREFIX = "____";
    public static final String METADATA_POSTFIX = "$";
    public static final String MARSHALLER_POSTFIX = "Marshaller";

    private EntityInfo entity;
    private List<String> imports = new ArrayList<String>();
    private String metadataClassName;
    private String metadataClassFullName;
    private List<Field> fields = new ArrayList<Field>();

    @Inject
    private ProcessingEnvironment processingEnv;

    @Inject
    private TypeHelper typeHelper;

    @Inject
    private MarshallerProvider2 marshallerProvider2;

    private StringBuilder mBuilder = new StringBuilder();
    private int mLevel = 0;


    public MetadataSourceFileImpl withEntity(EntityInfo entity) {
        this.entity = entity;

        addCommonImports();
        parseEntity();

        return this;
    }

    public MetadataSourceFileImpl addImport(Class<?> importClass) {
        addImport(importClass.getName());
        return this;
    }

    public MetadataSourceFileImpl addImport(String importName) {
        if (!imports.contains(importName)) {
            imports.add(importName);
        }
        return this;
    }

    public EntityInfo getEntity() {
        return entity;
    }

    public MetadataSourceFileImpl addField(Field field) {
        fields.add(field);
        for (String importName : field.getImports()) {
            addImport(importName);
        }
        return this;
    }

    private void addCommonImports() {
        addImport("android.database.Cursor");
        addImport("android.database.sqlite.SQLiteDatabase");
        addImport("org.brightify.torch.sql.statement.CreateTable");
        addImport("android.content.ContentValues");
        addImport("org.brightify.torch.EntityMetadata");
        addImport("org.brightify.torch.Key");
        addImport("org.brightify.torch.KeyFactory");
        addImport("org.brightify.torch.annotation.Entity");
        addImport("org.brightify.torch.util.MigrationAssistant");
        addImport("org.brightify.torch.util.MigrationException");
        addImport("org.brightify.torch.filter.NumberColumn");

        addImport("org.brightify.torch.sql.constraint.ColumnConstraint");
        addImport("org.brightify.torch.sql.ColumnDef");
        addImport("org.brightify.torch.sql.statement.CreateTable");
    }

    private void parseEntity() {
        metadataClassName = entity.name + METADATA_POSTFIX;
        metadataClassFullName = entity.fullName + METADATA_POSTFIX;

        for (Property property : entity.properties) {
            CursorMarshallerInfo marshallerInfo = typeHelper.getCursorMarshallerInfo(property);
            if (marshallerInfo == null) {
                // FIXME change to message through messager
                throw new IllegalStateException("Unsupported type " + property.getType() + "!");
            }
            ColumnInfo columnInfo = typeHelper.getColumnInfo(property);
            if (columnInfo == null) {
                throw new IllegalStateException("Unsupported type " + property.getType() + "!");
            }

            Field field = marshallerInfo.getField();
            field.setName(property.getColumnName() + MARSHALLER_POSTFIX);
            field.setFinal(true);
            field.setProtection(FieldImpl.Protection.PRIVATE);
            addField(field);

            Field columnField = columnInfo.getField(property);
            columnField.setProtection(FieldImpl.Protection.PUBLIC);
            columnField.setStatic(true);
            columnField.setFinal(true);
            columnField.setName(property.getName());
            addField(columnField);
        }
    }


    private void writeHeaderAndPackage() {
        append("/* Generated on ").append(new Date()).append(" by Torch */");
        if (entity.packageName != null && !entity.packageName.equals("")) {
            line("package ").append(entity.packageName).append(";");
            emptyLine();
        }
    }

    private void writeImports() {
        for (String importItem : imports) {
            line("import ").append(importItem).append(";");
        }
        emptyLine();
    }

    private void writeClass() {
        // TODO should the class be final?
        line("public final class ")
                .append(metadataClassName)
                .append(" implements EntityMetadata<")
                .append(entity.name)
                .append(">")
                .nest();

        emptyLine();

        writeStaticInformation();

        writeFields();

        writePrivateConstructor();

        writeCreateTable();
        writeFromCursor();
        writeToContentValues();
        writeMigrate();

        writeGetIdColumn();
        writeGetColumns();
        writeGetTableName();
        writeGetVersion();
        writeGetMigrationType();
        writeGetEntityId();
        writeSetEntityId();
        writeGetEntityClass();
        writeCreateKey();

        writeCreateMethod();

        unNest();
    }

    private void writeStaticInformation() {

    }

    private void writeFields() {
        for (Field field : fields) {
            field.write(this);
            emptyLine();
        }
    }

    private void writePrivateConstructor() {
        line("private ").append(metadataClassName).append("()").nest();
        unNest();
        emptyLine();
    }

    private void writeCreateTable() {
        CreateTable createTable = new CreateTable();
        createTable.setTableName(entity.tableName);
        for (Property property : entity.properties) {
            ColumnDef columnDef = new ColumnDef(property.getColumnName());
            Marshaller2<?> marshaller2 = marshallerProvider2.getMarshaller(typeHelper.classOf(property.getType()));

            columnDef.setTypeAffinity(marshaller2.getAffinity());
            if(property.getId() != null) {
                ColumnConstraint.PrimaryKey primaryKey = new ColumnConstraint.PrimaryKey();
                primaryKey.setAutoIncrement(property.getId().autoIncrement());
                columnDef.addColumnConstraint(primaryKey);
            }
            if(property.getNotNull() != null) {
                ColumnConstraint.NotNull notNull = new ColumnConstraint.NotNull();
                columnDef.addColumnConstraint(notNull);
            }
            if(property.getUnique() != null) {
                ColumnConstraint.Unique unique = new ColumnConstraint.Unique();
                columnDef.addColumnConstraint(unique);
            }
            createTable.addColumnDef(columnDef);
        }

        override();
        line("public void createTable(SQLiteDatabase db)").nest();
        // line("String sql = \"").append(createTable.toSQLString()).append("\";");
        // line("if(Settings.isQueryLoggingEnabled()).nest();
        // line("Log.d(\"Torch SQL\", sql);
        // unNest();
        line("db.execSQL(\"").append(createTable.toSQLString()).append("\");");
        unNest();
        emptyLine();
    }

    private void writeFromCursor() {
        override();
        line("public ").append(entity.name).append(" createFromCursor(Cursor cursor) throws Exception").nest();
        line(entity.name).append(" ").append(ENTITY).append(" = new ").append(entity.name).append("();");
        for (Property property : entity.properties) {
            Marshaller2<?> marshaller = marshallerProvider2.getMarshaller(typeHelper.classOf(property.getType()));
            line(marshaller.unmarshallingCode(property))
                    .append("; // ")
                    .append(property.getType());
        }
        line("return ").append(ENTITY).append(";");
        unNest();
        emptyLine();
    }

    private void writeToContentValues() {
        override();
        line("public ContentValues toContentValues(").append(entity.name).append(" ").append(ENTITY).append(") throws Exception").nest();
        line("ContentValues ").append(CONTENT_VALUES).append(" = new ContentValues();");
        for (Property property : entity.properties) {
            Marshaller2<?> marshaller = marshallerProvider2.getMarshaller(typeHelper.classOf(property.getType()));
            line(marshaller.marshallingCode(property)).append(";");
        }
        line("return ").append(CONTENT_VALUES).append(";");
        unNest();
        emptyLine();
    }

    private void writeMigrate() {
        override();
        line("public void migrate(MigrationAssistant<")
                .append(entity.name)
                .append("> assistant, String sourceVersion, String targetVersion) throws Exception")
                .nest();

        line("String migration = sourceVersion + \"->\" + targetVersion;");
        line("");
        // TODO generate switch and not if-else
        int i = 0;
        for (MigrationPath migrationPath : entity.migrationPaths) {
            if (i > 0) {
                append(" else ");
            }
            append("if(migration.equals(\"").append(migrationPath.getDescription()).append("\"))").nest();

            for (MigrationPathPart part = migrationPath.getStart(); part != null; part = part.getNext()) {
                line(entity.name)
                        .append(".")
                        .append(part.getMigrationMethod().getExecutable().getSimpleName())
                        .append("(assistant);");
            }

            unNest();
            i++;
        }
        if (i > 0) {
            append(" else ").nest();
        }
        line("throw new MigrationException(\"Unable to migrate entity! Could not find migration path from \" + " +
                "sourceVersion + \" to \" + targetVersion);");
        if (i > 0) {
            unNest();
        }
        unNest();
        emptyLine();
    }

    private void writeGetIdColumn() {
        override();
        line("public NumberColumn<Long> getIdColumn()").nest();
        line("return ").append(entity.idProperty.getName()).append(";");
        unNest();
        emptyLine();
    }

    private void writeGetColumns() {
        override();
        line("public String[] getColumns()").nest();
        line("return new String[] ").nest();
        int i = 0;
        for (Property property : entity.properties) {
            if (i > 0) {
                append(", ");
            }
            line("\"").append(property.getColumnName()).append("\"");
            i++;
        }
        unNest().append(";");
        unNest();
        emptyLine();
    }

    private void writeGetTableName() {
        override();
        line("public String getTableName()").nest();
        line("return \"").append(entity.tableName).append("\";");
        unNest();
        emptyLine();
    }

    private void writeGetVersion() {
        override();
        line("public String getVersion()").nest();
        line("return \"").append(entity.version).append("\";");
        unNest();
        emptyLine();
    }

    private void writeGetMigrationType() {
        override();
        line("public Entity.MigrationType getMigrationType()").nest();
        line("return ").append("Entity.MigrationType.").append(entity.migrationType).append(";");
        unNest();
        emptyLine();
    }

    private void writeGetEntityId() {
        override();
        line("public Long getEntityId(").append(entity.name).append(" entity)").nest();
        line("return entity.").append(entity.idProperty.getValue()).append(";");
        unNest();
        emptyLine();
    }

    private void writeSetEntityId() {
        override();
        line("public void setEntityId(").append(entity.name).append(" entity, Long id)").nest();
        line("entity.").append(entity.idProperty.setValue("id")).append(";");
        unNest();
        emptyLine();
    }

    private void writeGetEntityClass() {
        override();
        line("public Class<").append(entity.name).append("> getEntityClass()").nest();
        line("return ").append(entity.name).append(".class;");
        unNest();
        emptyLine();
    }

    private void writeCreateKey() {
        override();
        line("public Key<").append(entity.name).append("> createKey(").append(entity.name).append(" entity)").nest();
        line("return KeyFactory.create(getEntityClass(), getEntityId(entity));");
        unNest();
    }

    private void writeCreateMethod() {
        line("public static ").append(metadataClassName).append(" create()").nest();
        line("return new ").append(metadataClassName).append("();");
        unNest();
        emptyLine();
    }

    private void override() {
        line("@Override");
    }

    @Override
    public MetadataSourceFileImpl append(Object value) {
        mBuilder.append(value);
        return this;
    }

    @Override
    public MetadataSourceFileImpl line(Object value) {
        emptyLine();

        for(int i = 0; i < mLevel; i++) {
            mBuilder.append("    ");
        }
        mBuilder.append(value);

        return this;
    }

    public MetadataSourceFileImpl emptyLine() {
        mBuilder.append("\n");
        return this;
    }

    public MetadataSourceFileImpl nest() {
        mBuilder.append(" {");
        mLevel++;
        return this;
    }

    public MetadataSourceFileImpl newLineNest() {
        line("{");
        mLevel++;
        return this;
    }

    public MetadataSourceFileImpl unNest() {
        mLevel--;
        line("}");
        return this;
    }

    public MetadataSourceFileImpl nestWithoutBrackets() {
        mLevel++;
        return this;
    }

    public MetadataSourceFileImpl unNestWithoutBrackets() {
        mLevel--;
        return this;
    }

    public void save(String name) {
        writeHeaderAndPackage();
        writeImports();
        writeClass();

        try {
            Writer writer = processingEnv.getFiler().createSourceFile(name).openWriter();

            writer.write(mBuilder.toString());

            writer.flush();
            writer.close();

            mBuilder = new StringBuilder();
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

}
