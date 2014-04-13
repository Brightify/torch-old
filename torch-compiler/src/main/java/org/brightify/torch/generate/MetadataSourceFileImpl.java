package org.brightify.torch.generate;

import com.google.inject.Inject;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.EntityInfo;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.migration.MigrationPath;
import org.brightify.torch.compile.migration.MigrationPathPart;
import org.brightify.torch.marshall2.Marshaller;
import org.brightify.torch.marshall2.MarshallerProvider;
import org.brightify.torch.sql.ColumnDef;
import org.brightify.torch.sql.constraint.ColumnConstraint;
import org.brightify.torch.sql.statement.CreateTable;
import org.brightify.torch.filter.ColumnInfo;
import org.brightify.torch.util.TypeHelper;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
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
    private MarshallerProvider marshallerProvider;

    @Inject
    private EntityContext entityContext;

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
        addImport("org.brightify.torch.Torch");
    }

    private void parseEntity() {
        metadataClassName = entity.getSimpleName() + METADATA_POSTFIX;
        metadataClassFullName = entity.getFullName() + METADATA_POSTFIX;

        for (Property property : entity.getProperties()) {
            ColumnInfo columnInfo = null; // typeHelper.getColumnInfo(property);
            if (columnInfo == null) {
                throw new IllegalStateException("Unsupported type " + property.getType() + "!");
            }

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
        if (entity.getPackageName() != null && !entity.getPackageName().equals("")) {
            line("package ").append(entity.getPackageName()).append(";");
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
                .append(entity.getSimpleName())
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
        createTable.setTableName(entity.getTableName());
        for (Property property : entity.getProperties()) {

            Marshaller marshaller2 = marshallerProvider.getMarshaller(property);

            ColumnDef columnDef = new ColumnDef(property.getColumnName());
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
        line("public void createTable(Torch torch)").nest();
        // line("String sql = \"").append(createTable.toSQLString()).append("\";");
        // line("if(Settings.isQueryLoggingEnabled()).nest();
        // line("Log.d(\"Torch SQL\", sql);
        // unNest();
        line("torch.getDatabase().execSQL(\"").append(createTable.toSQLString()).append("\");");
        unNest();
        emptyLine();
    }

    private void writeFromCursor() {
        override();
        line("public ").append(entity.getSimpleName()).append(" createFromCursor(Torch torch, Cursor cursor) throws Exception").nest();
        line(entity.getSimpleName()).append(" ").append(ENTITY).append(" = new ").append(entity.getSimpleName()).append("();");
        for (Property property : entity.getProperties()) {
            Marshaller marshaller = marshallerProvider.getMarshaller(property);
            //line(marshaller.unmarshallingCode(property))
            //        .append("; // ")
            //        .append(property.getType());
        }
        line("return ").append(ENTITY).append(";");
        unNest();
        emptyLine();
    }

    private void writeToContentValues() {
        override();
        line("public ContentValues toContentValues(Torch torch, ").append(entity.getSimpleName()).append(" ").append(ENTITY).append(") throws Exception").nest();
        line("ContentValues ").append(CONTENT_VALUES).append(" = new ContentValues();");
        for (Property property : entity.getProperties()) {
            Marshaller marshaller = marshallerProvider.getMarshaller(property);
            //line(marshaller.marshallingCode(property)).append(";");
        }
        line("return ").append(CONTENT_VALUES).append(";");
        unNest();
        emptyLine();
    }

    private void writeMigrate() {
        override();
        line("public void migrate(MigrationAssistant<")
                .append(entity.getSimpleName())
                .append("> assistant, String sourceVersion, String targetVersion) throws Exception")
                .nest();

        line("String migration = sourceVersion + \"->\" + targetVersion;");
        line("");
        // TODO generate switch and not if-else
        int i = 0;
        for (MigrationPath migrationPath : entity.getMigrationPaths()) {
            if (i > 0) {
                append(" else ");
            }
            append("if(migration.equals(\"").append(migrationPath.getDescription()).append("\"))").nest();

            for (MigrationPathPart part = migrationPath.getStart(); part != null; part = part.getNext()) {
                line(entity.getSimpleName())
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
        line("return ").append(entity.getIdProperty().getName()).append(";");
        unNest();
        emptyLine();
    }

    private void writeGetColumns() {
        override();
        line("public String[] getColumns()").nest();
        line("return new String[] ").nest();
        int i = 0;
        for (Property property : entity.getProperties()) {
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
        line("return \"").append(entity.getTableName()).append("\";");
        unNest();
        emptyLine();
    }

    private void writeGetVersion() {
        override();
        line("public String getVersion()").nest();
        line("return \"").append(entity.getVersion()).append("\";");
        unNest();
        emptyLine();
    }

    private void writeGetMigrationType() {
        override();
        line("public Entity.MigrationType getMigrationType()").nest();
        line("return ").append("Entity.MigrationType.").append(entity.getMigrationType()).append(";");
        unNest();
        emptyLine();
    }

    private void writeGetEntityId() {
        override();
        line("public Long getEntityId(").append(entity.getSimpleName()).append(" ").append(ENTITY).append(")").nest();
        //line("return ").append(entity.getIdProperty().getGetter().getValue()).append(";");
        unNest();
        emptyLine();
    }

    private void writeSetEntityId() {
        override();
        line("public void setEntityId(").append(entity.getSimpleName()).append(" ").append(ENTITY).append (", Long id)").nest();
       // line(entity.getIdProperty().getSetter().setValue("id")).append(";");
        unNest();
        emptyLine();
    }

    private void writeGetEntityClass() {
        override();
        line("public Class<").append(entity.getSimpleName()).append("> getEntityClass()").nest();
        line("return ").append(entity.getSimpleName()).append(".class;");
        unNest();
        emptyLine();
    }

    private void writeCreateKey() {
        override();
        line("public Key<").append(entity.getSimpleName()).append("> createKey(").append(entity.getSimpleName()).append(" entity)").nest();
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
