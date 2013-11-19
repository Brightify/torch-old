package com.brightgestures.brightify;

import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.generate.EntityMetadataGenerator;
import com.brightgestures.brightify.generate.Field;
import com.brightgestures.brightify.parse.EntityInfo;
import com.brightgestures.brightify.parse.EntityParseException;
import com.brightgestures.brightify.parse.EntityParser;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.*;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@SupportedAnnotationTypes({ "com.brightgestures.brightify.annotation.Entity" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class BrightifyAnnotationProcessor extends AbstractProcessor {

    private static final List<String> testsss = new ArrayList<>();

    private final List<String> imports = new ArrayList<>();
    private final List<Field> fields = new ArrayList<>();

    public void addImport(Class<?> importClass) {
        addImport(importClass.getName());
    }

    public void addImport(String importName) {
        imports.add(importName);
    }

    public void addField(Field field) {
        fields.add(field);
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if(annotations.size() == 0) {
            return false;
        }

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Entity.class);

        EntityParser parser = new EntityParser(processingEnv);
        Set<EntityInfo> entityInfoSet = new HashSet<EntityInfo>();

        for(Element element : elements) {
            testsss.add(element.toString());
            try {
                // TODO move this somewhere else, maybe to metadata generator?
                Class.forName(element.toString() + "Metadata", false, ClassLoader.getSystemClassLoader());
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                        "Entity " + element.getSimpleName() + "already on classpath.", element);
                continue;
            } catch (ClassNotFoundException e) {
                // Not found means we need to create it.
            }

            try {
                entityInfoSet.add(parser.parseEntity(element));
            } catch (EntityParseException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.getElement());
            }
        }

        EntityMetadataGenerator metadataGenerator = new EntityMetadataGenerator(processingEnv);
        for(EntityInfo entityInfo : entityInfoSet) {
            metadataGenerator.generateMetadata(entityInfo);
        }

        // No more
        //EntityMetadataMapGenerator entityMetadataMapGenerator = new EntityMetadataMapGenerator(processingEnv);
        //entityMetadataMapGenerator.generateEntities(entityInfoSet);

        return true;
    }
/*
    public class EntitiesCreator extends SourceFileGenerator {
        private final Set<? extends Element> mElements;

        public EntitiesCreator(Set<? extends Element> elements) {
            mElements = elements;
        }

        public void processElements() {

            Map<Element, Boolean> ignored = new HashMap<Element, Boolean>();
            for(Element element : mElements) {
                boolean ignore = new EntityMapperCreator(element).processEntity();

                ignored.put(element, ignore);
            }


            append("/* Generated on ").append(new Date()).append(" by BrightifyAnnotationProcessor *");
            line("package com.brightgestures.brightify;");
            emptyLine();
            line("import java.util.Map;");
            line("import java.util.HashMap;");
            for(Element element : mElements) {
                if(ignored.get(element)) {
                    continue;
                }

                line("import ").append(element).append(";");
                line("import ").append(element).append("Metadata").append(";");
            }
            emptyLine();
            line("public class Entities").nest();
            line("private static Map<Class, EntityMetadata> sMetadatas = new HashMap<Class, EntityMetadata>();");
            emptyLine();
            line("static").nest();
            for(Element element : mElements) {
                if(ignored.get(element)) {
                    continue;
                }

                line("sMetadatas.put(").append(element.getSimpleName()).append(".class, new ")
                        .append(element.getSimpleName()).append("Metadata());");
            }
            unNest();
            unNest();

            save("com.brightgestures.brightify.Entities");
        }


    }

    public class EntityMapperCreator extends SourceFileGenerator {
        private final Element mElement;
        public EntityMapperCreator(Element element) {
            mElement = element;
        }

        public boolean processEntity() {
            EntityParser parser = new EntityParser(processingEnv);

            try {
                parser.parseEntity(mElement);
            } catch (EntityParseException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.getElement());
                return false;
            }
            Entity entityAnnotation = mElement.getAnnotation(Entity.class);
            if(entityAnnotation.ignore()) {
                return true;
            }

            String className = mElement.toString();

            String name = mElement.getSimpleName().toString();
            String fullName = mElement.toString();
            String metadataName = mElement.getSimpleName() + "Metadata";
            String metadataFullName = "com.brightgestures.brightify.metadata." + metadataName;

            List<? extends Element> children = mElement.getEnclosedElements();

            Types types = processingEnv.getTypeUtils();
            Elements elements = processingEnv.getElementUtils();

            Map<Element, Boolean> readableProperties;
            Map<Element, String> fieldSetterMapping;

            List<Property> properties = new ArrayList<Property>();

            for(Element child : children) {
                Property property = new Property();

            }

            append("/* Generated on ").append(new Date()).append(" by BrightifyAnnotationProcessor *");
            line("package com.brightgestures.brightify.metadata;");
            emptyLine();
            line("import ").append(fullName).append(";");
            emptyLine();
            line("import android.database.Cursor;");
            line("import android.content.ContentValues;");
            line("import com.brightgestures.brightify.EntityMetadata;");
            emptyLine();
            line("public class ").append(metadataName).append(" extends EntityMetadata<").append(name).append(">").nest();
            emptyLine();
            line("@Override");
            line("public void createTable(SQLiteDatabase db)").nest();
            line("CreateTable createTable = new CreateTable();");
            line("createTable.setTableName(").append(Helper.tableNameFromClassName(fullName)).append(");");
            for(Element child : children) {

            }

            unNest();
            emptyLine();
            line("@Override");
            line("public ").append(name).append(" createFromCursor(Cursor cursor)").nest();
            line(name).append(" entity = new ").append(name).append("();");
            emptyLine();
            for(Element child : children) {
                Ignore ignore = child.getAnnotation(Ignore.class);
                if(ignore != null) {
                    continue;
                }

                String childName = child.toString();
                if(child.getKind() == ElementKind.METHOD && (childName.startsWith("set"))) {

                }

                if(child.getKind().isField()) {
                    line("int ").append(child).append("Index = cursor.getColumnIndex(\"").append(child).append("\");");
                    line("entity.").append(child).append(" = cursor.get(").append(child).append("Index);");
                }/*

                if(TypeHelper.isAssignableFrom(Boolean.class, type)) {
                    value = cursor.getInt(index) > 0;
                } else if(TypeHelper.isAssignableFrom(Byte.class, type)) {
                    value = (byte) cursor.getInt(index);
                } else if(TypeHelper.isAssignableFrom(Short.class, type)) {
                    value = cursor.getShort(index);
                } else if(TypeHelper.isAssignableFrom(Integer.class, type)) {
                    value = cursor.getInt(index);
                } else if(TypeHelper.isAssignableFrom(Long.class, type)) {
                    value = cursor.getLong(index);
                } else if(TypeHelper.isAssignableFrom(Float.class, type)) {
                    value = cursor.getFloat(index);
                } else if(TypeHelper.isAssignableFrom(Double.class, type)) {
                    value = cursor.getDouble(index);
                } else if(TypeHelper.isAssignableFrom(String.class, type)) {
                    value = cursor.getString(index);
                } else if(TypeHelper.isAssignableFrom(byte[].class, type)) {
                    value = cursor.getBlob(index);
                }  else if(TypeHelper.isAssignableFrom(Key.class, type)) {
                    throw new UnsupportedOperationException("Not implemented!");
                } else if(TypeHelper.isAssignableFrom(Ref.class, type)) {
                    throw new UnsupportedOperationException("Not implemented!");
                } else if(TypeHelper.isAssignableFrom(Serializable.class, type)) {
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
*
               // processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, child + " of type " + child.asType(), child);

            }

            emptyLine();
            line("return entity;");
            unNest();

            line("@Override");
            line("public ContentValues toContentValues(").append(name).append(" entity)").nest();
            line("ContentValues values = new ContentValues();");

            for(Element child : children) {
                Ignore ignore = child.getAnnotation(Ignore.class);
                if(ignore != null) {
                    continue;
                }

                String childName = child.toString();
                String columnName = childName;

                if(child.getKind() == ElementKind.METHOD) {
                    Accessor accessor = child.getAnnotation(Accessor.class);
                    if(accessor != null && accessor.type() == Accessor.Type.GET) {
                        if(!accessor.name().equals("")) {
                            columnName = accessor.name();
                        } else {
                            columnName = child.getSimpleName().toString();
                        }

                        line("values.put(\"").append(columnName).append("\", entity.").append(childName).append(");").append(" // ").append(child.asType());
                    } else if(childName.startsWith("get") && childName.length() > 3) {
                        columnName = childName.substring(3);
                    }

                    // && (childName.startsWith("get") || childName.startsWith("is")
                } else if(child.getKind().isField()) {
                    TypeMirror childType = child.asType();

                    ArrayType t = types.getArrayType(types.getPrimitiveType(TypeKind.BYTE));
                    if(childType instanceof ArrayType) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, childType + " : " + t + " == " + (types.isSameType(childType, t) ? "yes" : "no"), child);
                    }
                    if(childType == typeOf(Key.class)) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Keys are not yet supported!", child);
                    } else if(childType == typeOf(Ref.class)) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Refs are not yet supported!", child);
                    } else if(
                                types.isSameType(childType, typeOf(Boolean.class)) ||
                                types.isSameType(childType, typeOf(Byte.class)) ||
                                types.isSameType(childType, typeOf(Short.class)) ||
                                types.isSameType(childType, typeOf(Integer.class)) ||
                                types.isSameType(childType, typeOf(Long.class)) ||
                                types.isSameType(childType, typeOf(Float.class)) ||
                                types.isSameType(childType, typeOf(Double.class)) ||
                                types.isSameType(childType, typeOf(String.class)) ||
                                (childType.getKind() == TypeKind.ARRAY &&
                                    types.isSameType(childType, types.getArrayType(types.getPrimitiveType(TypeKind.BYTE))))) {
                        line("values.put(\"").append(columnName).append("\", entity.").append(childName).append(");").append(" // ").append(child.asType());
                    } else if(types.isAssignable(childType, elements.getTypeElement("java.io.Serializable").asType())) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Serializable objects not yet supported!", child);
                    } else {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Type '" + child.asType() + "' is not supported!");
                    }

                    //processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, child + " assignable " + (child.asType() == elements.getTypeElement("java.lang.Long") ? "yes" : "no"), child);
                }
//

                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, child + " of type " + child.asType(),
                        child);
            }

            line("return values;");
            unNest();
            unNest();

            save(metadataFullName);

            return false;
        }

    }

    public static class Property {
        Element element;
        boolean writable;
        Type type;
        String name;

        enum Type {
            FIELD, METHOD
        }
    }*/

}
