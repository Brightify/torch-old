package com.brightgestures.brightify.parse;

import com.brightgestures.brightify.annotation.Accessor;
import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.annotation.Id;
import com.brightgestures.brightify.annotation.Ignore;
import com.brightgestures.brightify.annotation.Index;
import com.brightgestures.brightify.annotation.NotNull;
import com.brightgestures.brightify.annotation.Unique;
import com.brightgestures.brightify.util.Helper;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class EntityParser {

    private List<String> ignoredMethods = new ArrayList<String>();


    ProcessingEnvironment processingEnv;
    Messager messager;
    Types types;

    public EntityParser(ProcessingEnvironment processingEnvironment) {
        processingEnv = processingEnvironment;
        messager = processingEnvironment.getMessager();
        types = processingEnvironment.getTypeUtils();

        ignoredMethods.add("equals");
        ignoredMethods.add("toString");
    }

    // TODO refactor, make "messager.printMessage(ERROR, message, element) to be "throw new EntityParseException(message, element)
    public EntityInfo parseEntity(Element entity) throws EntityParseException {

        Entity entityAnnotation = entity.getAnnotation(Entity.class);
        if(entityAnnotation.ignore()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "Entity " + entity.getSimpleName() + " ignored, because @Entity.ignore is true.", entity);
            return null;
        }
        EntityInfo info = new EntityInfo();

        info.name = entity.getSimpleName().toString();
        info.fullName = entity.toString();
        String[] entityPackages = info.fullName.split("\\.");
        if(entityPackages.length == 1) {
            info.packageName = null;
        } else {
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < entityPackages.length - 1; i++) {
                if(i > 0) {
                    builder.append(".");
                }
                builder.append(entityPackages[i]);
            }
            info.packageName = builder.toString();
        }

        String tableName = info.fullName;
        if(!entityAnnotation.name().equals("")) {
            info.tableName = entityAnnotation.name();
        } else if(entityAnnotation.useSimpleName()) {
            info.tableName = info.name;
        }
        info.tableName = Helper.tableNameFromClassName(tableName); // TODO create own helper here in brightify-preprocessor
        info.delete = entityAnnotation.delete();
        info.version = entityAnnotation.version();
        info.migrationType = entityAnnotation.migration();


        Map<String, Property> propertyMap = new HashMap<String, Property>();
        List<? extends Element> children = entity.getEnclosedElements();

        for(Element child : children) {
            Set<Modifier> modifiers = child.getModifiers();
            boolean isPublic = false;
            for(Modifier modifier : modifiers) {
                if(modifier == Modifier.PUBLIC) {
                    isPublic = true;
                    break;
                }
            }
            if(!isPublic) {
                continue;
            }

            String childName = child.getSimpleName().toString();
            String childFullName = child.toString();

            Ignore ignore = child.getAnnotation(Ignore.class);
            if(ignore != null) {
                messager.printMessage(Diagnostic.Kind.NOTE, "Property " + childName + " ignored, because of @Ignore.", child);
                continue;
            }

            if(child.getKind() == ElementKind.FIELD) {
                if(propertyMap.containsKey(childName)) {
                    throw new EntityParseException(child, "Property %s already exists!", childName);
                }

                FieldProperty property = new FieldProperty();

                String columnName = childName;
                property.name = childName;
                property.fullName = childFullName;
                property.columnName = columnName;
                property.index = child.getAnnotation(Index.class) != null;
                property.id = child.getAnnotation(Id.class);
                property.notNull = child.getAnnotation(NotNull.class) != null;
                property.unique = child.getAnnotation(Unique.class) != null;

                property.type = child.asType();

                if(property.id != null) {
                    if(info.idProperty != null) {
                        throw new EntityParseException(child, "Duplicate @Id definition in entity %s!", entity.getSimpleName());
                    }
                    info.idProperty = property;
                }

                propertyMap.put(columnName, property);
            } else if (child.getKind() == ElementKind.METHOD) {
                Accessor accessor = child.getAnnotation(Accessor.class);
                Accessor.Type methodType;
                String columnName;
                ExecutableElement executableChild = (ExecutableElement) child;
                if(accessor != null) {
                    columnName = accessor.name();
                    methodType = accessor.type();
                } else if(childName.startsWith("get")) {
                    columnName = childName.substring(3, 4).toLowerCase() + childName.substring(4);
                    methodType = Accessor.Type.GET;
                } else if(childName.startsWith("set")) {
                    columnName = childName.substring(3, 4).toLowerCase() + childName.substring(4);
                    methodType = Accessor.Type.SET;
                } else if (childName.startsWith("is")) {
                    columnName = childName.substring(2, 3).toLowerCase() + childName.substring(3);
                    methodType = Accessor.Type.GET;
                } else {
                    if(!isMethodIgnored(childName)) { // We don't want to inform about methods from java.lang.Object
                        messager.printMessage(Diagnostic.Kind.NOTE, "Property " + childName + " ignored. It doesn't start with either get, set or is and is not annotated with @Accessor.", child);
                    }
                    continue;
                }

                boolean modification = false;
                Property abstractProperty = propertyMap.get(columnName);
                AccessorProperty property;
                if(abstractProperty == null) {
                    property = new AccessorProperty();
                } else if(abstractProperty instanceof AccessorProperty) {
                    property = (AccessorProperty) abstractProperty;
                    modification = true;
                } else {
                    throw new EntityParseException(child, "Property %s already defined by field!", columnName);
                }

                Index indexAnnotation = child.getAnnotation(Index.class);
                Id idAnnotation = child.getAnnotation(Id.class);
                NotNull notNullAnnotation = child.getAnnotation(NotNull.class);
                Unique uniqueAnnotation = child.getAnnotation(Unique.class);

                if(methodType == Accessor.Type.GET) {
                    if(property.getterName != null || property.getterFullName != null) {
                        throw new EntityParseException(child, "Getter for property %s already defined!", columnName);
                    }
                    if(executableChild.getParameters().size() != 0) {
                        throw new EntityParseException(child, "Getter %s mustn't have any parameters!", childFullName);
                    }
                    if(executableChild.getThrownTypes().size() != 0) {
                        throw new EntityParseException(child, "Getter %s mustn't have 'throws' declaration!", childFullName);
                    }

                    if(property.type == null) {
                        property.type = executableChild.getReturnType();
                    } else if (!types.isSameType(property.type, executableChild.getReturnType())) {
                        throw new EntityParseException(child, "Accessor types for property %s don't match!", columnName);
                    }

                    property.getterName = childName;
                    property.getterFullName = childFullName;

                    if(indexAnnotation != null) {
                        property.index = true;
                    }
                    if(idAnnotation != null) {
                        property.id = idAnnotation;
                    }
                    if(notNullAnnotation != null) {
                        property.notNull = true;
                    }
                    if(uniqueAnnotation != null) {
                        property.unique = true;
                    }

                } else if(methodType == Accessor.Type.SET) {
                    if(property.setterName != null || property.setterFullName != null) {
                        throw new EntityParseException(child, "Setter for property %s already defined", columnName);
                    }
                    if(executableChild.getParameters().size() != 1) {
                        throw new EntityParseException(child, "Setter %s has to have exactly one parameter!", childFullName);
                    }
                    if(executableChild.getThrownTypes().size() != 0) {
                        throw new EntityParseException(child, "Setter %s mustn't have 'throws' declaration!", childFullName);
                    }
                    // TODO should we care about return type or leave that to user?
                    // Tadeas Kriz [10/17/13] - I'm for not checking so user can return 'this' to chain setters
                    VariableElement parameter = executableChild.getParameters().get(0);
                    if(property.type == null) {
                        property.type = parameter.asType();
                    } else if(!types.isSameType(property.type, parameter.asType())) {
                        throw new EntityParseException(child, "Accessor types for property %s don't match!", columnName);
                    }

                    property.setterName = childName;
                    property.setterFullName = childFullName;

                    if(indexAnnotation != null) {
                        throw new EntityParseException(child, "@Index can't be on setter! Place it on getter instead.");
                    }
                    if(idAnnotation != null) {
                        throw new EntityParseException(child, "@Id can't be on setter! Place it on getter instead.");
                    }
                    if(notNullAnnotation != null) {
                        throw new EntityParseException(child, "@NotNull can't be on setter! Place it on getter instead.");
                    }
                    if(uniqueAnnotation != null) {
                        throw new EntityParseException(child, "@Unique can't be on setter! Place it on getter instead.");
                    }
                }

                property.columnName = columnName;

                if(property.id != null && info.idProperty != property) {
                    if(info.idProperty != null) {
                        throw new EntityParseException(child, "Duplicate @Id definition in entity %s!", entity.getSimpleName());
                    }
                    info.idProperty = property;
                }

                if(!modification) {
                    propertyMap.put(columnName, property);
                }
            }


        }

        for(Property property : propertyMap.values()) {
            if(property instanceof AccessorProperty) {
                AccessorProperty accessorProperty = (AccessorProperty) property;

                if(accessorProperty.getterName == null || accessorProperty.setterName == null) {
                    throw new EntityParseException(entity, "Entity %s contains accessor property with missing getter or setter!", entity.getSimpleName());
                }
            }
        }
        info.properties = new ArrayList<Property>(propertyMap.values());
        if(info.idProperty == null) {
            throw new EntityParseException(entity, "Entity %s doesn't contain a @Id Long field!", entity.getSimpleName());
        }

        return info;
    }

    private boolean isMethodIgnored(String name) {
        return ignoredMethods.contains(name);
    }

}
