package org.brightify.torch.parse;

import org.brightify.torch.annotation.Accessor;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;
import org.brightify.torch.annotation.Ignore;
import org.brightify.torch.annotation.Index;
import org.brightify.torch.annotation.Migration;
import org.brightify.torch.annotation.NotNull;
import org.brightify.torch.annotation.Unique;
import org.brightify.torch.util.Helper;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityParser {

    ProcessingEnvironment processingEnv;
    Messager messager;
    Types types;
    private List<String> ignoredMethods = new ArrayList<String>();

    public EntityParser(ProcessingEnvironment processingEnvironment) {
        processingEnv = processingEnvironment;
        messager = processingEnvironment.getMessager();
        types = processingEnvironment.getTypeUtils();

        ignoredMethods.add("equals");
        ignoredMethods.add("toString");
    }

    // TODO refactor, make "messager.printMessage(ERROR, message, element) to be "throw new EntityParseException
    // (message, element)
    public EntityInfo parseEntity(Element entity) throws EntityParseException {

        Entity entityAnnotation = entity.getAnnotation(Entity.class);
        if (entityAnnotation == null) {
            messager.printMessage(Diagnostic.Kind.WARNING, "Object " + entity.getSimpleName() + " passed in even " +
                                                           "though it's not annotated with @Entity!");
            return null;
        }
        if (entityAnnotation.ignore()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "Entity " + entity.getSimpleName() + " ignored, " +
                                                        "because @Entity.ignore is true.", entity);
            return null;
        }
        EntityInfo info = new EntityInfo();

        info.name = entity.getSimpleName().toString();
        info.fullName = entity.toString();
        String[] entityPackages = info.fullName.split("\\.");
        if (entityPackages.length == 1) {
            info.packageName = null;
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < entityPackages.length - 1; i++) {
                if (i > 0) {
                    builder.append(".");
                }
                builder.append(entityPackages[i]);
            }
            info.packageName = builder.toString();
        }

        String tableName = info.fullName;
        if (!entityAnnotation.name().equals("")) {
            info.tableName = entityAnnotation.name();
        } else if (entityAnnotation.useSimpleName()) {
            info.tableName = info.name;
        }
        info.tableName = Helper.tableNameFromClassName(tableName); // TODO create own helper here in
        // brightify-preprocessor
        info.delete = entityAnnotation.delete();
        info.version = entityAnnotation.version();
        info.migrationType = entityAnnotation.migration();


        Map<String, Property> propertyMap = new HashMap<String, Property>();
        List<? extends Element> children = entity.getEnclosedElements();

        for (Element child : children) {
            Set<Modifier> modifiers = child.getModifiers();
            if (modifiers.contains(Modifier.PRIVATE)) {
                continue;
            }

            String childName = child.getSimpleName().toString();
            String childFullName = child.toString();

            Ignore ignore = child.getAnnotation(Ignore.class);
            if (ignore != null) {
                messager.printMessage(Diagnostic.Kind.NOTE, "Child " + childName + " ignored, " +
                                                            "because of @Ignore.", child);
                continue;
            }

            if (child.getKind() == ElementKind.FIELD) {
                if(modifiers.contains(Modifier.FINAL) || modifiers.contains(Modifier.STATIC)) {
                    continue;
                }
                Property abstractProperty = propertyMap.get(childName);
                if (abstractProperty instanceof AccessorProperty) {
                    AccessorProperty property = (AccessorProperty) abstractProperty;
                    if (property.isForced()) {
                        messager.printMessage(Diagnostic.Kind.NOTE, "Child " + childName +
                                                                    " ignored, because it's already defined by " +
                                                                    "@Accessor method.", child);
                        continue;
                    }
                } else if (abstractProperty != null) {
                    throw new EntityParseException(child, "Property %s already exists!", childName);
                }

                FieldProperty property = new FieldProperty();

                String columnName = childName;
                property.setName(childName);
                property.setFullName(childFullName);
                property.setColumnName(columnName);
                property.setIndex(child.getAnnotation(Index.class));
                property.setId(child.getAnnotation(Id.class));
                property.setNotNull(child.getAnnotation(NotNull.class));
                property.setUnique(child.getAnnotation(Unique.class));

                property.setType(child.asType());

                if (property.getId() != null) {
                    if (info.idProperty != null) {
                        throw new EntityParseException(child, "Duplicate @Id definition in entity %s!",
                                                       entity.getSimpleName());
                    }
                    info.idProperty = property;
                }

                propertyMap.put(columnName, property);
            } else if (child.getKind() == ElementKind.METHOD) {
                Accessor accessor = child.getAnnotation(Accessor.class);
                Migration migration = child.getAnnotation(Migration.class);
                Accessor.Type methodType;
                String columnName;
                ExecutableElement executableChild = (ExecutableElement) child;
                if (accessor != null) {
                    columnName = accessor.name();
                    methodType = accessor.type();
                    if (methodType == Accessor.Type.INFERRED) {
                        if (types.isSameType(executableChild.getReturnType(), types.getNoType(TypeKind.VOID)) &&
                            executableChild.getParameters().size() == 1) {
                            methodType = Accessor.Type.SET;
                        } else if (!types.isSameType(executableChild.getReturnType(), types.getNoType(TypeKind.VOID)) &&
                                   executableChild.getParameters().size() == 0) {
                            methodType = Accessor.Type.GET;
                        } else {
                            throw new EntityParseException(child, "Could not infer accessor type!");
                        }
                    }
                    // FIXME to much repetetive code!
                    if (columnName.equals("")) {
                        if (methodType == Accessor.Type.GET && childName.startsWith("get")) {
                            columnName = childName.substring(3, 4).toLowerCase() + childName.substring(4);
                        } else if (methodType == Accessor.Type.GET && childName.startsWith("is")) {
                            columnName = childName.substring(2, 3).toLowerCase() + childName.substring(3);
                        } else if (methodType == Accessor.Type.SET && childName.startsWith("set")) {
                            columnName = childName.substring(3, 4).toLowerCase() + childName.substring(4);
                        } else {
                            columnName = childName;
                        }
                    }
                } else if (childName.startsWith("get")) {
                    columnName = childName.substring(3, 4).toLowerCase() + childName.substring(4);
                    methodType = Accessor.Type.GET;
                } else if (childName.startsWith("set")) {
                    columnName = childName.substring(3, 4).toLowerCase() + childName.substring(4);
                    methodType = Accessor.Type.SET;
                } else if (childName.startsWith("is")) {
                    columnName = childName.substring(2, 3).toLowerCase() + childName.substring(3);
                    methodType = Accessor.Type.GET;
                } else if (migration != null) {
                    if (!executableChild.getModifiers().contains(Modifier.STATIC)) {
                        throw new EntityParseException(child, "Migration method has to be static.");
                    }

                    List<? extends VariableElement> parameters = executableChild.getParameters();
                    if (parameters.size() != 1) {
                        throw new EntityParseException(child, "Migration method has to accept exactly one " +
                                                              "parameter. See documentation of @Migration.",
                                                       executableChild.getSimpleName());
                    }

                    if (migration.source().equals(migration.target())) {
                        throw new EntityParseException(child, "Source and target versions cannot equal.");
                    }

                    if (MigrationMethod.compare(migration.source(), migration.target()) >= 0) {
                        throw new EntityParseException(child, "Source version cannot be greater than target version.");
                    }

                    MigrationMethod migrationMethod = new MigrationMethod(executableChild, migration.preferred(),
                                                                          migration.source(), migration.target());

                    info.migrationMethods.add(migrationMethod);
                    continue;
                } else {
                    if (!isMethodIgnored(childName)) { // We don't want to inform about methods source java.lang.Object
                        messager.printMessage(Diagnostic.Kind.NOTE, "Child " + childName + " ignored. It doesn't " +
                                                                    "start with either get, " +
                                                                    "set or is and is not annotated with @Accessor.",
                                              child);
                    }
                    continue;
                }

                boolean modification = false;
                Property abstractProperty = propertyMap.get(columnName);
                AccessorProperty property;
                if (abstractProperty instanceof AccessorProperty) {
                    property = (AccessorProperty) abstractProperty;
                    if (accessor != null) {
                        property.setForced(true);
                    }
                    modification = true;
                } else if (abstractProperty == null || accessor != null) {
                    property = new AccessorProperty();
                    property.setForced(accessor != null);
                } else {
                    messager.printMessage(Diagnostic.Kind.NOTE, "Child " + childName +
                                                                " ignored, because there's already a field for this " +
                                                                "columnName.", child);
                    continue;
                }

                Index indexAnnotation = child.getAnnotation(Index.class);
                Id idAnnotation = child.getAnnotation(Id.class);
                NotNull notNullAnnotation = child.getAnnotation(NotNull.class);
                Unique uniqueAnnotation = child.getAnnotation(Unique.class);

                if (methodType == Accessor.Type.GET) {
                    if (property.getGetterName() != null || property.getGetterFullName() != null) {
                        throw new EntityParseException(child, "Getter for property %s already defined!", columnName);
                    }
                    if (executableChild.getParameters().size() != 0) {
                        throw new EntityParseException(child, "Getter %s mustn't have any parameters!", childFullName);
                    }
                    if (executableChild.getThrownTypes().size() != 0) {
                        throw new EntityParseException(child, "Getter %s mustn't have 'throws' declaration!",
                                                       childFullName);
                    }

                    if (property.getType() == null) {
                        property.setType(executableChild.getReturnType());
                    } else if (!types.isSameType(property.getType(), executableChild.getReturnType())) {
                        throw new EntityParseException(child, "Accessor types for property %s don't match!",
                                                       columnName);
                    }

                    property.setGetterName(childName);
                    property.setGetterFullName(childFullName);

                    property.setIndex(indexAnnotation);
                    property.setId(idAnnotation);
                    property.setNotNull(notNullAnnotation);
                    property.setUnique(uniqueAnnotation);

                } else if (methodType == Accessor.Type.SET) {
                    if (property.getSetterName() != null || property.getSetterFullName() != null) {
                        throw new EntityParseException(child, "Setter for property %s already defined", columnName);
                    }
                    if (executableChild.getParameters().size() != 1) {
                        throw new EntityParseException(child, "Setter %s has to have exactly one parameter!",
                                                       childFullName);
                    }
                    if (executableChild.getThrownTypes().size() != 0) {
                        throw new EntityParseException(child, "Setter %s mustn't have 'throws' declaration!",
                                                       childFullName);
                    }
                    // TODO should we care about return type or leave that to user?
                    // Tadeas Kriz [10/17/13] - I'm for not checking so user can return 'this' to chain setters
                    VariableElement parameter = executableChild.getParameters().get(0);
                    if (property.getType() == null) {
                        property.setType(parameter.asType());
                    } else if (!types.isSameType(property.getType(), parameter.asType())) {
                        throw new EntityParseException(child, "Accessor types for property %s don't match!",
                                                       columnName);
                    }

                    property.setSetterName(childName);
                    property.setSetterFullName(childFullName);

                    if (indexAnnotation != null) {
                        throw new EntityParseException(child, "@Index can't be on setter! Place it on getter instead.");
                    }
                    if (idAnnotation != null) {
                        throw new EntityParseException(child, "@Id can't be on setter! Place it on getter instead.");
                    }
                    if (notNullAnnotation != null) {
                        throw new EntityParseException(child, "@NotNull can't be on setter! Place it on getter " +
                                                              "instead.");
                    }
                    if (uniqueAnnotation != null) {
                        throw new EntityParseException(child, "@Unique can't be on setter! Place it on getter instead" +
                                                              ".");
                    }
                }

                property.setColumnName(columnName);

                if (property.getId() != null && info.idProperty != property) {
                    if (info.idProperty != null) {
                        throw new EntityParseException(child, "Duplicate @Id definition in entity %s!",
                                                       entity.getSimpleName());
                    }
                    info.idProperty = property;
                }

                if (!modification) {
                    propertyMap.put(columnName, property);
                }
            }


        }

        for (Property property : propertyMap.values()) {
            if (property instanceof AccessorProperty) {
                AccessorProperty accessorProperty = (AccessorProperty) property;

                if (accessorProperty.getGetterName() == null || accessorProperty.getSetterName() == null) {
                    throw new EntityParseException(entity, "Entity %s contains accessor property with missing getter " +
                                                           "or setter!", entity.getSimpleName());
                }
            }
        }
        info.properties = new ArrayList<Property>(propertyMap.values());
        if (info.idProperty == null) {
            throw new EntityParseException(entity, "Entity %s doesn't contain a @Id Long field!",
                                           entity.getSimpleName());
        }

        checkMigrationMethods(info);

        return info;
    }

    private List<MigrationPathPart> findMigrationPaths(List<MigrationMethod> allMigrationMethods,
                                                       MigrationMethod target,
                                                       MigrationMethod current) {
        if (current.equals(target)) {
            MigrationPathPart lastBit = new MigrationPathPart();
            lastBit.migrationMethod = current;
            return Collections.singletonList(lastBit);
        }

        ArrayList<MigrationPathPart> returnPaths = new ArrayList<MigrationPathPart>();
        for (MigrationMethod migrationMethod : allMigrationMethods) {
            if (migrationMethod.getFromVersion().equals(current.getToVersion())) {
                List<MigrationPathPart> possiblePaths = findMigrationPaths(allMigrationMethods, target,
                                                                           migrationMethod);
                for (MigrationPathPart possiblePath : possiblePaths) {
                    MigrationPathPart thisBit = new MigrationPathPart();
                    thisBit.migrationMethod = current;
                    thisBit.next = possiblePath;
                    possiblePath.previous = thisBit;
                    returnPaths.add(thisBit);
                }

            }
        }
        return returnPaths;
    }

    private List<MigrationPath> pickMostEffectivePaths(Map<String, List<MigrationPathPart>> pathPartsMap) {
        List<MigrationPath> effectivePaths = new ArrayList<MigrationPath>();
        for (String description : pathPartsMap.keySet()) {
            List<MigrationPathPart> pathParts = pathPartsMap.get(description);
            if (pathParts.size() == 0) {
                continue;
            }
            MigrationPath mostEffectivePath = null;
            for (MigrationPathPart pathPart : pathParts) {
                MigrationPath path = new MigrationPath();
                path.description = description;
                path.start = pathPart;
                if (mostEffectivePath == null) {
                    mostEffectivePath = path;
                    continue;
                }

                // FIXME should we select the path if all parts are prefered?
                // FIXME or should we improve the algorithm to check the difference between cost and preference?
                // if(path.getCost() == path.getPreferredPartsCount())

                int currentCost = path.getCost();
                int mostEffectiveCost = mostEffectivePath.getCost();
                if (currentCost < mostEffectiveCost) {
                    mostEffectivePath = path;
                } else if (currentCost == mostEffectiveCost &&
                           path.getPreferredPartsCount() > mostEffectivePath.getPreferredPartsCount()) {
                    mostEffectivePath = path;
                }
            }
            effectivePaths.add(mostEffectivePath);
        }
        return effectivePaths;
    }

    private void checkMigrationMethods(EntityInfo entity) throws EntityParseException {
        String sourceVersion = Entity.LOWEST_VERSION;
        String targetVersion = entity.version;

        Map<String, List<MigrationPathPart>> migrationPaths = new HashMap<String, List<MigrationPathPart>>();
        for (MigrationMethod sourceMethod : entity.migrationMethods) {
            for (MigrationMethod targetMethod : entity.migrationMethods) {
                if (MigrationMethod.compare(sourceMethod.getFromVersion(), targetMethod.getToVersion()) > 0) {
                    continue;
                }

                String key = sourceMethod.getFromVersion() + "->" + targetMethod.getToVersion();

                if (!migrationPaths.containsKey(key)) {
                    List<MigrationPathPart> listToSave = new ArrayList<MigrationPathPart>();
                    migrationPaths.put(key, listToSave);
                }
                List<MigrationPathPart> savedPaths = migrationPaths.get(key);

                if (sourceMethod == targetMethod) {
                    MigrationPathPart migrationPath = new MigrationPathPart();
                    migrationPath.migrationMethod = sourceMethod;

                    savedPaths.add(migrationPath);
                    continue;
                }

                List<MigrationPathPart> foundPaths = findMigrationPaths(entity.migrationMethods, targetMethod,
                                                                        sourceMethod);

                savedPaths.addAll(foundPaths);
            }
        }

        if (!sourceVersion.equals(targetVersion) &&
            (!migrationPaths.containsKey(sourceVersion + "->" + targetVersion) ||
             migrationPaths.get(sourceVersion + "->" + targetVersion).size() == 0)) {
            throw new EntityParseException(entity.element,
                                           "Entity %s doesn't have migration path to cover migration from %s to %s",
                                           entity.name, sourceVersion, targetVersion);
        }

        entity.migrationPaths = pickMostEffectivePaths(migrationPaths);

        String brake = "";

        // FIXME move to "verifyEntity()"
        /*
        if (entity.version < Entity.LOWEST_VERSION) {
            throw new EntityParseException(entity.element, "Entity version cannot be lower than %d.",
                                           Entity.LOWEST_VERSION);
        }

        if (entity.version == Entity.LOWEST_VERSION) {
            return;
        }

        int lowestSource = Integer.MAX_VALUE;
        int highestTarget = Integer.MIN_VALUE;
        for (MigrationMethod migrationMethod : entity.migrationMethods) {
            if (migrationMethod.getFromVersion() < lowestSource) {
                lowestSource = migrationMethod.getFromVersion();
            }
            if (migrationMethod.getToVersion() > highestTarget) {
                highestTarget = migrationMethod.getToVersion();
            }
        }

        if (lowestSource < Entity.LOWEST_VERSION) {
            throw new EntityParseException(entity.element, "Entity has a migration method with source version lower " +
                                                           "than %d", Entity.LOWEST_VERSION);
        }

        if (highestTarget < entity.version) {
            throw new EntityParseException(entity.element, "Entity doesn't have migration methods up to it's version.");
        }

        if (highestTarget > entity.version) {
            throw new EntityParseException(entity.element,
                                           "There is a migration method with target version higher than entity's " +
                                           "version. Did you forget to bump the version?");
        }

        boolean[] sourceVersions = new boolean[highestTarget - sourceVersion]; // 1 -> highest - 1
        boolean[] targetVersions = new boolean[highestTarget - sourceVersion]; // 2 -> highest

        for (MigrationMethod migrationMethod : entity.migrationMethods) {
            for (int i = migrationMethod.getFromVersion(); i <= migrationMethod.getToVersion(); i++) {
                if (i < migrationMethod.getToVersion()) {
                    if (sourceVersions[i - sourceVersion]) {
                        throw new EntityParseException(migrationMethod.getExecutable(),
                                                       "Duplicate migration coverage for entity version %d", i);
                    }
                    sourceVersions[i - sourceVersion] = true;
                }
                if (i > migrationMethod.getFromVersion()) {
                    if (targetVersions[i - sourceVersion - 1]) {
                        throw new EntityParseException(migrationMethod.getExecutable(),
                                                       "Duplicate migration coverage for entity version %d", i);
                    }
                    targetVersions[i - sourceVersion - 1] = true;
                }
            }
        }

        for (int i = 0; i < sourceVersions.length; i++) {
            if (!sourceVersions[i]) {
                throw new EntityParseException(entity.element, "Missing migration coverage for entity version %d",
                                               i + sourceVersion);
            }
        }

        for (int i = 0; i < targetVersions.length; i++) {
            if (!targetVersions[i]) {
                throw new EntityParseException(entity.element, "Missing migration coverage for entity version %d",
                                               i + sourceVersion);
            }
        }

        Collections.sort(entity.migrationMethods, new Comparator<MigrationMethod>() {
            @Override
            public int compare(MigrationMethod lhs, MigrationMethod rhs) {
                return Integer.compare(lhs.getFromVersion(), rhs.getFromVersion());
            }
        });*/
    }

    private boolean isMethodIgnored(String name) {
        return ignoredMethods.contains(name);
    }

}
