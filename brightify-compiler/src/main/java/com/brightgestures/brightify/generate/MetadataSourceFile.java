package com.brightgestures.brightify.generate;

import com.brightgestures.brightify.SourceFile;
import com.brightgestures.brightify.marshall.CursorMarshallerInfo;
import com.brightgestures.brightify.parse.EntityInfo;
import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.util.TypeHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class MetadataSourceFile extends SourceFile {
    private static final String METADATA_POSTFIX = "Metadata";
    private static final String MARSHALLER_POSTFIX = "Marshaller";

    private final TypeHelper typeHelper;
    private final EntityInfo entity;
    private List<String> imports = new ArrayList<>();
    private String metadataClassName;
    private String metadataClassFullName;
    private List<Field> fields = new ArrayList<>();

    public MetadataSourceFile(TypeHelper typeHelper, EntityInfo entity) {
        super(typeHelper.getProcessingEnvironment());
        this.typeHelper = typeHelper;
        this.entity = entity;
        addCommonImports();
        parseEntity();
    }

    public MetadataSourceFile addImport(Class<?> importClass) {
        addImport(importClass.getName());
        return this;
    }

    public MetadataSourceFile addImport(String importName) {
        imports.add(importName);
        return this;
    }

    public EntityInfo getEntity() {
        return entity;
    }

    public MetadataSourceFile addField(Field field) {
        fields.add(field);
        for(String importName : field.getImports()) {
            addImport(importName);
        }
        return this;
    }

    private void addCommonImports() {
        addImport("android.database.Cursor");
        addImport("android.database.sqlite.SQLiteDatabase");
        addImport("com.brightgestures.brightify.sql.statement.CreateTable");
        addImport("android.content.ContentValues");
        addImport("com.brightgestures.brightify.EntityMetadata");
        addImport("com.brightgestures.brightify.Key");

        addImport("com.brightgestures.brightify.sql.constraint.ColumnConstraint");
        addImport("com.brightgestures.brightify.sql.ColumnDef");
        addImport("com.brightgestures.brightify.sql.statement.CreateTable");
    }

    private void parseEntity() {
        metadataClassName = entity.name + METADATA_POSTFIX;
        metadataClassFullName = entity.fullName + METADATA_POSTFIX;

        for (Property property : entity.properties) {
            CursorMarshallerInfo marshallerInfo = typeHelper.getCursorMarshallerInfo(property);
            if (marshallerInfo == null) {
                // FIXME change to message through messager
                throw new IllegalStateException("Unsupported type " + property.type + "!");
            }
            Field field = marshallerInfo.getField();
            field.setName(property.columnName + MARSHALLER_POSTFIX);
            field.setFinal(true);
            field.setProtection(Field.Protection.PRIVATE);
            addField(field);
        }
    }

    @Override
    public void save(String name) {
        writeHeaderAndPackage();
        writeImports();
        writeClass();

        super.save(name);
    }

    private void writeHeaderAndPackage() {
        append("/* Generated on ").append(new Date()).append(" by Brightify */");
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
                .append(" extends EntityMetadata<")
                .append(entity.name)
                .append(">")
                .nest();

        emptyLine();

        writeStaticInformation();

        writeFields();

        writeCreateTable();
        writeFromCursor();
        writeToContentValues();

        writeGetIdColumnName();
        writeGetColumns();
        writeGetTableName();
        writeGetEntityId();
        writeSetEntityId();
        writeGetEntityClass();
        writeCreateKey();

        writeCreateMethod();

        unNest();
    }

    private void writeStaticInformation() {
        line("public static final String idColumnName = \"").append(entity.idProperty.columnName).append("\";");
        line("public static final String[] columns =").nest();
        int i = 0;
        for (Property property : entity.properties) {
            if (i > 0) {
                append(", ");
            }
            line("\"").append(property.columnName).append("\"");
            i++;
        }
        unNest().append(";");
        line("public static final String tableName = \"").append(entity.tableName).append("\";");
        line("public static final Class<").append(entity.name).append("> entityClass = ").append(entity.name).append(
                ".class;");
        emptyLine();
    }

    private void writeFields() {
        for (Field field : fields) {
            field.write(this);
            emptyLine();
        }
    }

    private void writeCreateTable() {
        override();
        line("public void createTable(SQLiteDatabase db)").nest();
        line("CreateTable createTable = new CreateTable();");
        line("createTable.setTableName(tableName);");
        for (Property property : entity.properties) {
            newLineNest();
            line("ColumnDef columnDef = new ColumnDef();");
            line("columnDef.setName(\"").append(property.columnName).append("\");");
            line("columnDef.setTypeAffinity(").append(property.columnName).append(MARSHALLER_POSTFIX)
                    .append(".getAffinity());");
            emptyLine();
            if (property.id != null) {
                line("ColumnConstraint.PrimaryKey primaryKey = new ColumnConstraint.PrimaryKey();");
                line("primaryKey.setAutoIncrement(").append(property.id.autoIncrement()).append(");");
                line("primaryKey.setColumnName(columnDef.getName());");
                line("columnDef.addColumnConstraint(primaryKey);");
                emptyLine();
            }

            if (property.notNull) {
                line("ColumnConstraint.NotNull notNull = new ColumnConstraint.NotNull();");
                line("notNull.setColumnName(columnDef.getName());");
                line("columnDef.addColumnConstraint(notNull);");
                emptyLine();
            }

            if (property.unique) {
                line("ColumnConstraint.Unique unique = new ColumnConstraint.Unique();");
                line("unique.setColumnName(columnDef.getName());");
                line("columnDef.addColumnConstraint(unique);");
                emptyLine();
            }

            line("createTable.addColumnDef(columnDef);");
            unNest();
        }
        line("createTable.run(db);");
        unNest();
        emptyLine();
    }

    private void writeFromCursor() {
        override();
        line("public ").append(entity.name).append(" createFromCursor(Cursor cursor) throws Exception").nest();
        line(entity.name).append(" entity = new ").append(entity.name).append("();");
        for (Property property : entity.properties) {
            line("entity.").append(property.setValue(
                    property.columnName + MARSHALLER_POSTFIX + ".unmarshall(cursor, \"" + property.columnName + "\")"))
                    .append("; // ")
                    .append(property.type);
        }
        line("return entity;");
        unNest();
        emptyLine();
    }

    private void writeToContentValues() {
        override();
        line("public ContentValues toContentValues(").append(entity.name).append(" entity) throws Exception").nest();
        line("ContentValues values = new ContentValues();");
        for (Property property : entity.properties) {
            line(property.columnName)
                    .append(MARSHALLER_POSTFIX)
                    .append(".marshall(values, \"")
                    .append(property.columnName)
                    .append("\", entity.")
                    .append(property.getValue())
                    .append(");");
        }
        line("return values;");
        unNest();
        emptyLine();
    }

    private void writeGetIdColumnName() {
        override();
        line("public String getIdColumnName()").nest();
        line("return idColumnName;");
        unNest();
        emptyLine();
    }

    private void writeGetColumns() {
        override();
        line("public String[] getColumns()").nest();
        line("return columns;");
        unNest();
        emptyLine();
    }

    private void writeGetTableName() {
        override();
        line("public String getTableName()").nest();
        line("return tableName;");
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
        line("return entityClass;");
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

}
