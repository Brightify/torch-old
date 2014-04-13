package org.brightify.torch.compile;

import com.google.inject.Inject;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.util.TypeHelper;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;

import javax.annotation.PostConstruct;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityContextImpl implements EntityContext {

    @Inject
    private Reflections reflections;

    @Inject
    private TypeHelper typeHelper;

    @Inject
    private Messager messager;

    private Set<Class<?>> entityClasses = new HashSet<Class<?>>();

    private Set<EntityInfo> entityInfos = new HashSet<EntityInfo>();

    public EntityContextImpl() {

    }

    @PostConstruct
    private void init() {
        Set<String> metadataClassNames = reflections.getStore().getSubTypesOf(EntityMetadata.class.getCanonicalName());

        for (String metadataClassName : metadataClassNames) {
            Class<?> actualMetadataClass;
            try {
                actualMetadataClass = ReflectionUtils.forName(metadataClassName);
            } catch (ReflectionsException e) {
                messager.printMessage(Diagnostic.Kind.WARNING, "Skipping metadata class " + metadataClassName + " because it could not be loaded.");
                continue;
            }

            if(Modifier.isAbstract(actualMetadataClass.getModifiers())) {
                messager.printMessage(Diagnostic.Kind.WARNING , "Skipping metadata class " + metadataClassName + " because it is abstract.");
                continue;
            }

            Map<TypeVariable<?>, Type> params =
                    typeHelper.genericParameters(EntityMetadata.class, actualMetadataClass, null);
            Type entityType = params.values().iterator().next();
            if(entityType instanceof Class<?>) {
                registerEntityClass((Class<?>) entityType);
            } else {
                messager.printMessage(Diagnostic.Kind.WARNING, "Skipping metadata class " + metadataClassName + " because it does not state entity class!");
            }
        }
    }

    @Override
    public Set<Class<?>> getEntityClasses() {
        return entityClasses;
    }

    @Override
    public Set<EntityInfo> getEntityInfos() {
        return entityInfos;
    }

    @Override
    public void registerEntityInfo(EntityInfo entityInfo) {
        entityInfos.add(entityInfo);
        messager.printMessage(Diagnostic.Kind.NOTE, "Registered entity info " + entityInfo.getFullName() + " to entity context.");
    }

    @Override
    public void registerEntityClass(Class<?> entityClass) {
        entityClasses.add(entityClass);
        messager.printMessage(Diagnostic.Kind.NOTE, "Registered entity class " + entityClass.getCanonicalName() + " to entity context.");
    }

    @Override
    public boolean containsEntity(String name) {
        for (Class<?> entityClass : entityClasses) {
            if (entityClass.getName().equals(name)) {
                return true;
            }
        }

        for (EntityInfo entityInfo : entityInfos) {
            if (entityInfo.getFullName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsEntity(Element element) {
        return containsEntity(element.toString());
    }

    @Override
    public boolean containsEntity(Class<?> entityClass) {
        return containsEntity(entityClass.getName());
    }

    @Override
    public boolean containsEntity(Property property) {
        return containsEntity(property.getType().toString());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("EntityContext contents: \n");

        builder.append("Class entities: [\n");
        int i = 0;
        for (Class<?> entityClass : entityClasses) {
            if(i > 0) {
                builder.append(", \n");
            }
            builder.append("\t").append(entityClass.getCanonicalName());
            i++;
        }
        builder.append("]\n");

        builder.append("Source entities: [\n");
        i = 0;
        for (EntityInfo entityInfo : entityInfos) {
            if(i > 0) {
                builder.append(", \n");
            }
            builder.append("\t").append(entityInfo.getFullName());
            i++;
        }
        builder.append("]\n");

        return builder.toString();
    }
}
