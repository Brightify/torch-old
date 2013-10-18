package com.brightgestures.brightify.generate;

import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Ref;
import com.brightgestures.brightify.SourceFileGenerator;
import com.brightgestures.brightify.annotation.Accessor;
import com.brightgestures.brightify.annotation.Ignore;
import com.brightgestures.brightify.parse.EntityInfo;
import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.util.Helper;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Date;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class EntityMetadataGenerator extends SourceFileGenerator {
    public EntityMetadataGenerator(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }

    public void generateMetadata(EntityInfo entity) {

        String metadataName = entity.name + "Metadata";
        String metadataFullName = "com.brightgestures.brightify.metadata." + metadataName;

        Types types = processingEnv.getTypeUtils();
        Elements elements = processingEnv.getElementUtils();


        append("/* Generated on ").append(new Date()).append(" by BrightifyAnnotationProcessor */");
        line("package com.brightgestures.brightify.metadata;");
        emptyLine();
        line("import ").append(entity.fullName).append(";");
        emptyLine();
        line("import android.database.Cursor;");
        line("import android.database.sqlite.SQLiteDatabase;");
        line("import com.brightgestures.brightify.sql.statement.CreateTable;");
        line("import android.content.ContentValues;");
        line("import com.brightgestures.brightify.EntityMetadata;");
        emptyLine();
        line("public class ").append(metadataName).append(" extends EntityMetadata<").append(entity.name).append(">").nest();
        emptyLine();
        line("@Override");
        line("public void createTable(SQLiteDatabase db)").nest();
        line("CreateTable createTable = new CreateTable();");
        line("createTable.setTableName(\"").append(Helper.tableNameFromClassName(entity.fullName)).append("\");");
        for(Property property : entity.properties) {

        }

        unNest();
        emptyLine();
        line("@Override");
        line("public ").append(entity.name).append(" createFromCursor(Cursor cursor)").nest();
        line(entity.name).append(" entity = new ").append(entity.name).append("();");
        emptyLine();
        for(Property property : entity.properties) {
            line("int ").append(property.columnName).append("Index = cursor.getColumnIndex(\"").append(property.columnName).append("\");");
            line("entity.").append(property.setValue("cursor.get(" + property.columnName + "Index)")).append(";");
            emptyLine();

            
            /*

                if(TypeUtils.isAssignableFrom(Boolean.class, type)) {
                    value = cursor.getInt(index) > 0;
                } else if(TypeUtils.isAssignableFrom(Byte.class, type)) {
                    value = (byte) cursor.getInt(index);
                } else if(TypeUtils.isAssignableFrom(Short.class, type)) {
                    value = cursor.getShort(index);
                } else if(TypeUtils.isAssignableFrom(Integer.class, type)) {
                    value = cursor.getInt(index);
                } else if(TypeUtils.isAssignableFrom(Long.class, type)) {
                    value = cursor.getLong(index);
                } else if(TypeUtils.isAssignableFrom(Float.class, type)) {
                    value = cursor.getFloat(index);
                } else if(TypeUtils.isAssignableFrom(Double.class, type)) {
                    value = cursor.getDouble(index);
                } else if(TypeUtils.isAssignableFrom(String.class, type)) {
                    value = cursor.getString(index);
                } else if(TypeUtils.isAssignableFrom(byte[].class, type)) {
                    value = cursor.getBlob(index);
                }  else if(TypeUtils.isAssignableFrom(Key.class, type)) {
                    throw new UnsupportedOperationException("Not implemented!");
                } else if(TypeUtils.isAssignableFrom(Ref.class, type)) {
                    throw new UnsupportedOperationException("Not implemented!");
                } else if(TypeUtils.isAssignableFrom(Serializable.class, type)) {
                    try {
                        value = Serializer.deserialize(cursor.getBlob(index));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new IllegalStateException("Type '" + type.toString() + "' cannot be restored from database!");
                }
*/
               // processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, child + " of type " + child.asType(), child);

            }
            line("return entity;");
            unNest();

            line("@Override");
            line("public ContentValues toContentValues(").append(entity.name).append(" entity)").nest();
            line("ContentValues values = new ContentValues();");

            for(Property property : entity.properties) {

                if(property.type == typeOf(Key.class)) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Keys are not yet supported!", entity.element); // TODO: should be property.element!
                } else if(types.isSameType(property.type, typeOf(Ref.class))) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Refs are not yet supported!", entity.element); // TODO: should be property.element!
                } else if(
                            types.isSameType(property.type, typeOf(Boolean.class)) ||
                            types.isSameType(property.type, typeOf(Byte.class)) ||
                            types.isSameType(property.type, typeOf(Short.class)) ||
                            types.isSameType(property.type, typeOf(Integer.class)) ||
                            types.isSameType(property.type, typeOf(Long.class)) ||
                            types.isSameType(property.type, typeOf(Float.class)) ||
                            types.isSameType(property.type, typeOf(Double.class)) ||
                            types.isSameType(property.type, typeOf(String.class)) ||
                            (property.type.getKind() == TypeKind.ARRAY &&
                                types.isSameType(property.type, types.getArrayType(types.getPrimitiveType(TypeKind.BYTE))))) {
                    line("values.put(\"").append(property.columnName).append("\", entity.").append(property.getValue()).append(");").append(" // ").append(property.type);
                } else if(types.isAssignable(property.type, elements.getTypeElement("java.io.Serializable").asType())) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Serializable objects not yet supported!", entity.element); // TODO: should be property.element!
                } else {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Type '" + property.type + "' is not supported!");
                }

                    //processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, child + " assignable " + (child.asType() == elements.getTypeElement("java.lang.Long") ? "yes" : "no"), child);
            }

            line("return values;");
            unNest();
            unNest();

            save(metadataFullName);

    }


    private TypeMirror typeOf(Class cls) {
        Element element = processingEnv.getElementUtils().getTypeElement(cls.getName());
        return element.asType();
    }
}
