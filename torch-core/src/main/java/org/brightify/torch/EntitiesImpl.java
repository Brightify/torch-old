package org.brightify.torch;

import org.brightify.torch.util.Constants;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntitiesImpl implements Entities {

    private Map<Class<?>, EntityDescription<?>> byClass = new HashMap<Class<?>, EntityDescription<?>>();
    private Map<String, EntityDescription<?>> byTableName = new HashMap<String, EntityDescription<?>>();

    @Override
    public <ENTITY> Key<ENTITY> keyOf(ENTITY entity) {
        @SuppressWarnings("unchecked")
        Class<ENTITY> entityClass = (Class<ENTITY>) entity.getClass();
        EntityDescription<ENTITY> metadata = getDescription(entityClass);

        return KeyFactory.create(entityClass, metadata.getIdProperty().get(entity));
    }

    public void clear() {
        byClass.clear();
        byTableName.clear();
    }

    @Override
    public Collection<EntityDescription<?>> getAllDescriptions() {
        return byClass.values();
    }

    @Override
    public <ENTITY> EntityDescription<ENTITY> getDescription(Class<ENTITY> entityClass) {
        return (EntityDescription<ENTITY>) byClass.get(entityClass);
    }

    @Override
    public <ENTITY> EntityDescription<ENTITY> getDescription(String name) {
        return (EntityDescription<ENTITY>) byTableName.get(name);
    }

    @Override
    public <ENTITY> void registerMetadata(EntityDescription<ENTITY> metadata) {
        Class<ENTITY> entityClass = metadata.getEntityClass();
        String tableName = metadata.getSafeName();
        if (!byClass.containsKey(entityClass)) { // TODO should this be IllegalArgumentException ?
            byClass.put(entityClass, metadata);
            // throw new IllegalStateException("This entity's metadata are already registered! Class: " + entityClass
            // .getName());
        }
        if (!byTableName.containsKey(tableName)) {
            byTableName.put(tableName, metadata);
            // throw new IllegalStateException("This table name is already registered! Table name:" + tableName);
        }
    }

    public static EntityDescription<?> resolveEntityDescription(Class<?> entityOrEntityDescriptionClass) {
        Class<EntityDescription<?>> descriptionClass;
        if(EntityDescription.class.isAssignableFrom(entityOrEntityDescriptionClass)) {
            descriptionClass = (Class<EntityDescription<?>>) entityOrEntityDescriptionClass;
        } else {
            String descriptionName = entityOrEntityDescriptionClass.getName() + Constants.DESCRIPTION_POSTFIX;
            try {
                descriptionClass = (Class<EntityDescription<?>>) Class.forName(descriptionName);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Entity description '" + descriptionName + "' does not exist!", e);
            }
        }

        return instantiateEntityDescription(descriptionClass);
    }

    public static EntityDescription<?> instantiateEntityDescription(Class<EntityDescription<?>> descriptionClass) {
        try {
            Constructor<EntityDescription<?>> constructor = descriptionClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Could not instantiate entity description '" + descriptionClass.getSimpleName() + "'.", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Constructor not accessible for entity description '" + descriptionClass.getSimpleName() + "'.", e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Entity description '" + descriptionClass.getSimpleName() + "' lacks a no-arg constructor.", e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Could not invoke constructor for entity description '" + descriptionClass.getSimpleName() + "'.", e);
        }
    }

}
