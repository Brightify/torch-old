package org.brightify.torch.compile.parse;

import com.google.inject.Inject;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Ignore;
import org.brightify.torch.annotation.Migration;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.EntityMirror;
import org.brightify.torch.compile.migration.MigrationMethod;
import org.brightify.torch.compile.migration.MigrationMethodImpl;
import org.brightify.torch.compile.migration.MigrationPath;
import org.brightify.torch.compile.migration.MigrationPathImpl;
import org.brightify.torch.compile.migration.MigrationPathPart;
import org.brightify.torch.compile.migration.MigrationPathPartImpl;
import org.brightify.torch.compile.util.TypeHelper;
import org.brightify.torch.parse.EntityParseException;
import org.brightify.torch.util.MigrationAssistant;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class MigrationParserImpl implements MigrationParser {

    private static final List<String> ignoredMethods = Arrays.asList("equals", "toString");

    @Inject
    private Messager messager;

    @Inject
    private Types types;

    @Inject
    private TypeHelper typeHelper;

    @Inject
    private EntityContext entityContext;

    @Override
    public List<MigrationPath> parseEntityElement(Element element, EntityMirror entity) {
        List<MigrationMethod> migrationMethods = new ArrayList<MigrationMethod>();
        for (Element property : element.getEnclosedElements()) {
            parseMigrationMethods(migrationMethods, property, entity);
        }

        return checkMigrationMethods(element, entity, migrationMethods);
    }

    private void parseMigrationMethods(List<MigrationMethod> list, Element element, EntityMirror entity) {
        if (verify(element, entity)) {
            return;
        }

        ExecutableElement executable = (ExecutableElement) element;

        Migration migration = element.getAnnotation(Migration.class);
        if (migration.source() == migration.target()) {
            throw new EntityParseException(element, "Source and target versions cannot equal.");
        }

        if (migration.source() > migration.target()) {
            throw new EntityParseException(element, "Source version cannot be greater than target version.");
        }

        MigrationMethodImpl migrationMethod = new MigrationMethodImpl();
        migrationMethod.setExecutable(executable);
        migrationMethod.setPreferred(migration.preferred());
        migrationMethod.setSourceRevision(migration.source());
        migrationMethod.setTargetRevision(migration.target());
        list.add(migrationMethod);
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
                MigrationPathImpl path = new MigrationPathImpl();
                path.setDescription(description);
                path.setStart(pathPart);

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

    private List<MigrationPathPartImpl> findMigrationPaths(List<MigrationMethod> allMigrationMethods,
                                                           MigrationMethod target,
                                                           MigrationMethod current) {
        if (current.equals(target)) {
            MigrationPathPartImpl lastBit = new MigrationPathPartImpl();
            lastBit.setMigrationMethod(current);
            return Collections.singletonList(lastBit);
        }

        ArrayList<MigrationPathPartImpl> returnPaths = new ArrayList<MigrationPathPartImpl>();
        for (MigrationMethod migrationMethod : allMigrationMethods) {
            if (migrationMethod.sourceRevision() == current.targetRevision()) {
                List<MigrationPathPartImpl> possiblePaths = findMigrationPaths(allMigrationMethods, target,
                                                                               migrationMethod);
                for (MigrationPathPartImpl possiblePath : possiblePaths) {
                    MigrationPathPartImpl thisBit = new MigrationPathPartImpl();
                    thisBit.setMigrationMethod(current);
                    thisBit.setNext(possiblePath);
                    possiblePath.setPrevious(thisBit);
                    returnPaths.add(thisBit);
                }

            }
        }
        return returnPaths;
    }

    private List<MigrationPath> checkMigrationMethods(Element element, EntityMirror entity,
                                                      List<MigrationMethod> migrationMethods)
            throws EntityParseException {
        long sourceVersion = Entity.DEFAULT_REVISION;
        long targetVersion = entity.getRevision();

        Map<String, List<MigrationPathPart>> migrationPaths = new HashMap<String, List<MigrationPathPart>>();
        for (MigrationMethod sourceMethod : migrationMethods) {
            for (MigrationMethod targetMethod : migrationMethods) {
                if (sourceMethod.sourceRevision() > targetMethod.targetRevision()) {
                    continue;
                }

                String key = sourceMethod.sourceRevision() + "->" + targetMethod.targetRevision();

                if (!migrationPaths.containsKey(key)) {
                    List<MigrationPathPart> listToSave = new ArrayList<MigrationPathPart>();
                    migrationPaths.put(key, listToSave);
                }
                List<MigrationPathPart> savedPaths = migrationPaths.get(key);

                if (sourceMethod == targetMethod) {
                    MigrationPathPartImpl migrationPath = new MigrationPathPartImpl();
                    migrationPath.setMigrationMethod(sourceMethod);

                    savedPaths.add(migrationPath);
                    continue;
                }

                List<MigrationPathPartImpl> foundPaths =
                        findMigrationPaths(migrationMethods, targetMethod, sourceMethod);

                savedPaths.addAll(foundPaths);
            }
        }

        if (sourceVersion != targetVersion &&
            (!migrationPaths.containsKey(sourceVersion + "->" + targetVersion) ||
             migrationPaths.get(sourceVersion + "->" + targetVersion).size() == 0)) {
            throw new EntityParseException(element,
                                           "Entity doesn't have migration path to cover migration from %s to %s",
                                           sourceVersion, targetVersion);
        }

        return pickMostEffectivePaths(migrationPaths);

        //String brake = "";

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

    private boolean verify(Element element, EntityMirror entity) throws EntityParseException {
        String name = element.getSimpleName().toString();
        Set<Modifier> modifiers = element.getModifiers();
        if (element.getAnnotation(Migration.class) == null) {
            return true;
        }
        if (element.getAnnotation(Ignore.class) != null) {
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Element " + name +
                                  " ignored because it was marked with @Ignore annotation.", element
            );
            return true;
        }
        if (ignoredMethods.contains(name)) {
            throw new EntityParseException(element, "");
        }
        if (element.getKind() != ElementKind.METHOD) {
            throw new EntityParseException(element, "Only methods can be annotated with @Migration!");
        }
        if (modifiers.contains(Modifier.PRIVATE)) {
            throw new EntityParseException(element,
                                           "Migration methods cannot be private! Recommended visibility is package " +
                                           "(no modifier).");
        }
        if (modifiers.contains(Modifier.PUBLIC) || modifiers.contains(Modifier.PROTECTED)) {
            messager.printMessage(Diagnostic.Kind.WARNING,
                                  "Recommended visibility for migration methods is package (no modifier).");
        }

        ExecutableElement executable = (ExecutableElement) element;
        List<? extends VariableElement> parameters = executable.getParameters();

        if (parameters.size() != 1 || !types.isSameType(parameters.get(0).asType(), types.getDeclaredType(typeHelper.elementOf(MigrationAssistant.class) ,entity.getElement().asType()))) {
            throw new EntityParseException(element,
                                           "Migration method has to have exactly one parameter of type " +
                                           "MigrationAssistant.");
        }

        return false;
    }

}
