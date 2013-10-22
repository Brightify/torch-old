package com.brightgestures.brightify.generate;

import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Ref;
import com.brightgestures.brightify.SourceFileGenerator;
import com.brightgestures.brightify.parse.EntityInfo;
import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.util.Helper;
import com.brightgestures.brightify.util.TypeHelper;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Date;
import java.util.UUID;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class EntityMetadataGenerator extends SourceFileGenerator {
    public EntityMetadataGenerator(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }

    public void generateMetadata(EntityInfo entity) {

        String metadataPostfix = "Metadata";
        String metadataName = entity.name + metadataPostfix;
        String metadataFullName = entity.fullName + metadataPostfix;
        String internalMetadataName = metadataFullName.replaceAll("\\.", "_");
        String internalMetadataFullName = "com.brightgestures.brightify.metadata." + internalMetadataName;

        Types types = processingEnv.getTypeUtils();
        Elements elements = processingEnv.getElementUtils();

        append("/* Generated on ").append(new Date()).append(" by EntityMetadataGenerator */");
        if(entity.packageName != null && !entity.packageName.equals("")) {
            line("package ").append(entity.packageName).append(";");
            emptyLine();
        }
        line("import ").append(entity.fullName).append(";");
        emptyLine();
        line("import android.database.Cursor;");
        line("import android.database.sqlite.SQLiteDatabase;");
        line("import com.brightgestures.brightify.sql.statement.CreateTable;");
        line("import android.content.ContentValues;");
        line("import com.brightgestures.brightify.EntityMetadata;");
        emptyLine();
        line("import com.brightgestures.brightify.sql.affinity.IntegerAffinity;");
        line("import com.brightgestures.brightify.sql.affinity.NoneAffinity;");
        line("import com.brightgestures.brightify.sql.affinity.NumericAffinity;");
        line("import com.brightgestures.brightify.sql.affinity.RealAffinity;");
        line("import com.brightgestures.brightify.sql.affinity.TextAffinity;");
        line("import com.brightgestures.brightify.sql.constraint.ColumnConstraint;");
        line("import com.brightgestures.brightify.sql.ColumnDef;");
        line("import com.brightgestures.brightify.sql.statement.CreateTable;");
        emptyLine();
        // TODO should the class be final?
        line("public final class ").append(metadataName).append(" extends EntityMetadata<").append(entity.name).append(">").nest();
        emptyLine();
        line("public static final String idColumnName = \"").append(entity.idProperty.columnName).append("\";");
        line("public static final String[] columns =").nest();
        int i = 0;
        for(Property property : entity.properties) {
            if(i > 0) {
                append(", ");
            }
            line("\"").append(property.columnName).append("\"");
            i++;
        }
        unNest().append(";");
        line("public static final String tableName = \"").append(entity.tableName).append("\";");
        line("public static final Class<").append(entity.name).append("> entityClass = ").append(entity.name).append(".class;");
        emptyLine();
        line("@Override");
        line("public String getIdColumnName()").nest();
        line("return idColumnName;");
        unNest();
        emptyLine();
        line("@Override");
        line("public String[] getColumns()").nest();
        line("return columns;");
        unNest();
        emptyLine();
        line("@Override");
        line("public String getTableName()").nest();
        line("return tableName;");
        unNest();
        emptyLine();
        line("@Override");
        line("public Long getEntityId(").append(entity.name).append(" entity)").nest();
        line("return entity.").append(entity.idProperty.getValue()).append(";");
        unNest();
        emptyLine();
        line("@Override");
        line("public void setEntityId(").append(entity.name).append(" entity, Long id)").nest();
        line("entity.").append(entity.idProperty.setValue("id")).append(";");
        unNest();
        emptyLine();
        line("@Override");
        line("public Class<").append(entity.name).append("> getEntityClass()").nest();
        line("return entityClass;");
        unNest();
        emptyLine();
        line("@Override");
        line("public void createTable(SQLiteDatabase db)").nest();
        line("CreateTable createTable = new CreateTable();");
        line("createTable.setTableName(\"").append(Helper.tableNameFromClassName(entity.fullName)).append("\");");
        for(Property property : entity.properties) {
            newLineNest();

            line("ColumnDef columnDef = new ColumnDef();");
            line("columnDef.setName(\"").append(property.columnName).append("\");");
            String typeAffinity = TypeHelper.affinityFromTypeMirror(property.type);
            if(typeAffinity == null) {
                throw new RuntimeException("Unsupported type " + property.type + "!"); // throw?
            }
            line("columnDef.setTypeName(").append(typeAffinity).append(");");
            emptyLine();
            if(property.id != null) {
                line("ColumnConstraint.PrimaryKey primaryKey = new ColumnConstraint.PrimaryKey();");
                line("primaryKey.setAutoIncrement(").append(property.id.autoIncrement()).append(");");
                line("primaryKey.setColumnName(columnDef.getName());");
                line("columnDef.addColumnConstraint(primaryKey);");
                emptyLine();
            }

            if(property.notNull) {
                line("ColumnConstraint.NotNull notNull = new ColumnConstraint.NotNull();");
                line("notNull.setColumnName(columnDef.getName());");
                line("columnDef.addColumnConstraint(notNull);");
            }

            if(property.unique) {
                line("ColumnConstraint.Unique unique = new ColumnConstraint.Unique();");
                line("unique.setColumnName(columnDef.getName());");
                line("columnDef.addColumnConstraint(unique);");
            }

            line("createTable.addColumnDef(columnDef);");
            unNest();
            emptyLine();
        }
        emptyLine();
        line("createTable.run(db);");
        unNest();
        emptyLine();
        line("@Override");
        line("public ").append(entity.name).append(" createFromCursor(Cursor cursor)").nest();
        line(entity.name).append(" entity = new ").append(entity.name).append("();");
        emptyLine();
        for(Property property : entity.properties) {
            TypeMirror propertyType = property.type;
            if(propertyType.getKind().isPrimitive()) {
                propertyType = types.boxedClass((PrimitiveType) propertyType).asType();
            }

            newLineNest();
            // TODO think of a way to not add this line when type is not supported
            line("int index = cursor.getColumnIndex(\"").append(property.columnName).append("\");");
            if(types.isSameType(propertyType, typeOf(Key.class))) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Keys are not yet supported!", entity.element); // TODO: should be property.element!
            } else if(types.isSameType(propertyType, typeOf(Ref.class))) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Refs are not yet supported!", entity.element); // TODO: should be property.element!
            } else if(types.isSameType(propertyType, typeOf(Boolean.class))) {
                line("entity.").append(property.setValue("cursor.getInt(index) > 0")).append(";");
            } else if(types.isSameType(propertyType, typeOf(Byte.class))) {
                line("entity.").append(property.setValue("(Byte) cursor.getInt(index)")).append(";");
            } else if(types.isSameType(propertyType, typeOf(Short.class))) {
                line("entity.").append(property.setValue("cursor.getShort(index)")).append(";");
            } else if(types.isSameType(propertyType, typeOf(Integer.class))) {
                line("entity.").append(property.setValue("cursor.getInt(index)")).append(";");
            } else if(types.isSameType(propertyType, typeOf(Long.class))) {
                line("entity.").append(property.setValue("cursor.getLong(index)")).append(";");
            } else if(types.isSameType(propertyType, typeOf(Float.class))) {
                line("entity.").append(property.setValue("cursor.getFloat(index)")).append(";");
            } else if(types.isSameType(propertyType, typeOf(Double.class))) {
                line("entity.").append(property.setValue("cursor.getDouble(index)")).append(";");
            } else if(types.isSameType(propertyType, typeOf(String.class))) {
                line("entity.").append(property.setValue("cursor.getString(index)")).append(";");
            } else if(propertyType.getKind() == TypeKind.ARRAY &&
                types.isSameType(propertyType, types.getArrayType(types.getPrimitiveType(TypeKind.BYTE)))) {
                line("entity.").append(property.setValue("cursor.getBlob(index)")).append(";");
            } else if(types.isAssignable(propertyType, elements.getTypeElement("java.io.Serializable").asType())) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Serializable objects not yet supported!", entity.element); // TODO: should be property.element!
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Type " + property.type + " is not supported!", entity.element); // TODO: should be property.element!
            }
            unNest();
            emptyLine();
        }
        line("return entity;");
        unNest();

        line("@Override");
        line("public ContentValues toContentValues(").append(entity.name).append(" entity)").nest();
        line("ContentValues values = new ContentValues();");

        for(Property property : entity.properties) {
            TypeMirror propertyType = property.type;
            if(propertyType.getKind().isPrimitive()) {
                propertyType = types.boxedClass((PrimitiveType) propertyType).asType();
            }

            if(types.isSameType(propertyType, typeOf(Key.class))) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Keys are not yet supported!", entity.element); // TODO: should be property.element!
            } else if(types.isSameType(propertyType, typeOf(Ref.class))) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Refs are not yet supported!", entity.element); // TODO: should be property.element!
            } else if(
                        types.isSameType(propertyType, typeOf(Boolean.class)) ||
                        types.isSameType(propertyType, typeOf(Byte.class)) ||
                        types.isSameType(propertyType, typeOf(Short.class)) ||
                        types.isSameType(propertyType, typeOf(Integer.class)) ||
                        types.isSameType(propertyType, typeOf(Long.class)) ||
                        types.isSameType(propertyType, typeOf(Float.class)) ||
                        types.isSameType(propertyType, typeOf(Double.class)) ||
                        types.isSameType(propertyType, typeOf(String.class)) ||
                        (propertyType.getKind() == TypeKind.ARRAY &&
                            types.isSameType(propertyType, types.getArrayType(types.getPrimitiveType(TypeKind.BYTE))))) {
                line("values.put(\"").append(property.columnName).append("\", entity.").append(property.getValue()).append(");").append(" // ").append(property.type);
            } else if(types.isAssignable(propertyType, elements.getTypeElement("java.io.Serializable").asType())) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Serializable objects not yet supported!", entity.element); // TODO: should be property.element!
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Type " + property.type + " is not supported!", entity.element); // TODO: should be property.element!
            }
        }

        line("return values;");
        unNest();
        emptyLine();
        line("public static ").append(metadataName).append(" create()").nest();
        line("return new ").append(metadataName).append("();");
        unNest();
        unNest();

        save(metadataFullName);

        append("/* Generated on ").append(new Date()).append(" by EntityMetadataGenerator */");
        line("package com.brightgestures.brightify.metadata;");
        emptyLine();
        line("import ").append(metadataFullName).append(";");
        line("import com.brightgestures.brightify.internal.EntityMetadataProvider;");
        line("import com.brightgestures.brightify.Entities;");
        emptyLine();
        line("public class ").append(internalMetadataName).append(" implements EntityMetadataProvider").nest();
        emptyLine();
        line("static").nest();
        line("Entities.registerMetadata(").append(metadataName).append(".create());");
        unNest();
        unNest();

        save(internalMetadataFullName);
    }


    private TypeMirror typeOf(Class cls) {
        Element element = processingEnv.getElementUtils().getTypeElement(cls.getName());
        return element.asType();
    }
}
