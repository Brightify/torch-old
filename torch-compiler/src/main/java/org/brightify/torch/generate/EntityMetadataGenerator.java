package org.brightify.torch.generate;

import org.brightify.torch.SourceFileGenerator;
import org.brightify.torch.parse.EntityInfo;
import org.brightify.torch.util.TypeHelper;
import org.brightify.torch.util.TypeHelperImpl;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Date;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class EntityMetadataGenerator extends SourceFileGenerator {

    private final TypeHelper typeHelper;

    public EntityMetadataGenerator(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);

        this.typeHelper = new TypeHelperImpl(processingEnvironment);
    }

    public void generateMetadata(EntityInfo entity) {

        String metadataPostfix = "Metadata";
        String metadataName = entity.name + metadataPostfix;
        String metadataFullName = entity.fullName + metadataPostfix;
        String internalMetadataName = metadataFullName.replaceAll("\\.", "_");
        String internalMetadataFullName = "com.brightgestures.brightify.metadata." + internalMetadataName;

        MetadataSourceFile sourceFile = new MetadataSourceFile(typeHelper, entity);
        sourceFile.save(metadataFullName);


        /*
        Types types = processingEnv.getTypeUtils();
        Elements elements = processingEnv.getElementUtils();

        append("/* Generated on ").append(new Date()).append(" by EntityMetadataGenerator /");
        if(entity.packageName != null && !entity.packageName.equals("")) {
            line("package ").append(entity.packageName).append(";");
            emptyLine();
        }
        line("import ").append(entity.fullName).append(";");
        emptyLine();
        line("import android.database.Cursor;");
        line("import android.database.sqlite.SQLiteDatabase;");
        line("import org.brightify.torch.sql.statement.CreateTable;");
        line("import android.content.ContentValues;");
        line("import org.brightify.torch.EntityMetadata;");
        line("import org.brightify.torch.Key;");
        line("import com.brightgestures.brightify.util.Serializer;");
        emptyLine();
        line("import org.brightify.torch.sql.constraint.ColumnConstraint;");
        line("import org.brightify.torch.sql.ColumnDef;");
        line("import org.brightify.torch.sql.statement.CreateTable;");
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
            String typeAffinity = ""; // typeHelper.affinityFromProperty(property);
            if(typeAffinity == null) {
                throw new RuntimeException("Unsupported type " + property.type + "!"); // throw?
            }
            line("columnDef.setTypeAffinity(").append(typeAffinity).append(");");
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
        line("public ").append(entity.name).append(" createFromCursor(Cursor cursor) throws Exception").nest();
        line(entity.name).append(" entity = new ").append(entity.name).append("();");
        emptyLine();
        for(Property property : entity.properties) {
            SupportedType supportedTypeSet = null; // typeHelper.supportedTypeSet(property);
            if(supportedTypeSet == null) {
                continue;
            }

            newLineNest();
            line("int index = cursor.getColumnIndex(\"").append(property.columnName).append("\");");
            supportedTypeSet.read(property, this);
            unNest();
            emptyLine();


            TypeMirror propertyType = property.type;
            if(propertyType.getKind().isPrimitive()) {
                propertyType = types.boxedClass((PrimitiveType) propertyType).asType();
            }

            newLineNest();
            // TODO think of a way to not add this line when type is not supported
            line("int index = cursor.getColumnIndex(\"").append(property.columnName).append("\");");
            if(types.isSameType(propertyType, typeOf(Key.class))) {
                //processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Keys are not yet supported!", entity.element); // TODO: should be property.element!
                line("entity.").append(property.setValue("Key.keyFromByteArray(cursor.getBlob(index)")).append(";").append(" // ").append(property.type);
            } else if(types.isSameType(propertyType, typeOf(Ref.class))) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Refs are not yet supported!", entity.element); // TODO: should be property.element!
                // line("entity.").append(property.setValue("")).append(";");
            } else if(types.isSameType(propertyType, typeOf(Boolean.class))) {
                line("entity.").append(property.setValue("cursor.getInt(index) > 0")).append(";").append(" // ").append(property.type);
            } else if(types.isSameType(propertyType, typeOf(Byte.class))) {
                line("entity.").append(property.setValue("(Byte) cursor.getInt(index)")).append(";").append(" // ").append(property.type);
            } else if(types.isSameType(propertyType, typeOf(Short.class))) {
                line("entity.").append(property.setValue("cursor.getShort(index)")).append(";").append(" // ").append(property.type);
            } else if(types.isSameType(propertyType, typeOf(Integer.class))) {
                line("entity.").append(property.setValue("cursor.getInt(index)")).append(";").append(" // ").append(property.type);
            } else if(types.isSameType(propertyType, typeOf(Long.class))) {
                line("entity.").append(property.setValue("cursor.getLong(index)")).append(";").append(" // ").append(property.type);
            } else if(types.isSameType(propertyType, typeOf(Float.class))) {
                line("entity.").append(property.setValue("cursor.getFloat(index)")).append(";").append(" // ").append(property.type);
            } else if(types.isSameType(propertyType, typeOf(Double.class))) {
                line("entity.").append(property.setValue("cursor.getDouble(index)")).append(";").append(" // ").append(property.type);
            } else if(types.isSameType(propertyType, typeOf(String.class))) {
                line("entity.").append(property.setValue("cursor.getString(index)")).append(";").append(" // ").append(property.type);
            } else if(propertyType.getKind() == TypeKind.ARRAY &&
                types.isSameType(propertyType, types.getArrayType(types.getPrimitiveType(TypeKind.BYTE)))) {
                line("entity.").append(property.setValue("cursor.getBlob(index)")).append(";").append(" // ").append(property.type);
            } else if(types.isAssignable(propertyType, elements.getTypeElement("java.util.Collection").asType())) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Collections not yet supported! Type: " + propertyType, entity.element);
            } else if(types.isAssignable(propertyType, elements.getTypeElement("java.io.Serializable").asType())) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Serializable objects not yet supported!", entity.element); // TODO: should be property.element!
                line("entity.").append(property.setValue("Serializer.deserialize(cursor.getBlob(index)," + propertyType + ".class)")).append(";").append(" // ").append(property.type);
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Type " + property.type + " is not supported!", entity.element); // TODO: should be property.element!
            }
        }
        line("return entity;");
        unNest();

        line("@Override");
        line("public ContentValues toContentValues(").append(entity.name).append(" entity) throws Exception").nest();
        line("ContentValues values = new ContentValues();");

        for(Property property : entity.properties) {
            SupportedType supportedTypeSet = null; // typeHelper.supportedTypeSet(property);
            if(supportedTypeSet == null) {
                continue;
            }

            newLineNest();
            supportedTypeSet.write(property, this);
            unNest();
            emptyLine();



            TypeMirror propertyType = property.type;
            if(propertyType.getKind().isPrimitive()) {
                propertyType = types.boxedClass((PrimitiveType) propertyType).asType();
            }

            if(types.isSameType(propertyType, typeOf(Key.class))) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Keys are not yet supported!", entity.element); // TODO: should be property.element!
                line("values.put(\"").append(property.columnName).append("\", Key.keyToByteArray(entity.").append(property.getValue()).append("));").append(" // ").append(property.type);
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
        */
        //save(metadataFullName);

        append("/* Generated on ").append(new Date()).append(" by EntityMetadataGenerator */");
        line("package com.brightgestures.brightify.metadata;");
        emptyLine();
        line("import ").append(metadataFullName).append(";");
        line("import org.brightify.torch.Entities;");
        emptyLine();
        line("public class ").append(internalMetadataName).nest();
        emptyLine();
        line("static").nest();
        line("Entities.registerMetadata(").append(metadataName).append(".create());");
        unNest();
        unNest();

//        save(internalMetadataFullName);
    }
}
