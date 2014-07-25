package org.brightify.torch;

import org.brightify.torch.annotation.Entity;
import org.brightify.torch.filter.NumberProperty;
import org.brightify.torch.filter.Property;
import org.brightify.torch.util.Helper;
import org.brightify.torch.util.MigrationAssistant;

import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface EntityDescription<ENTITY> {
    /**
     * Returns the ID property.
     */
    NumberProperty<Long> getIdProperty();

    /**
     * Returns an array of properties.
     */
    Property<?>[] getProperties();

    /**
     * Returns a safe class name.
     *
     * @see Helper#safeNameFromClass(Class) for meaning of the safety.
     */
    String getSafeName();

    String getVersion();

    Entity.MigrationType getMigrationType();

    Long getEntityId(ENTITY entity);

    void setEntityId(ENTITY entity, Long id);

    Class<ENTITY> getEntityClass();

    Key<ENTITY> createKey(ENTITY entity);

    ENTITY createFromRawEntity(TorchFactory torchFactory, ReadableRawEntity rawEntity, Set<Class<?>> loadGroups)
            throws Exception;

    void toRawEntity(TorchFactory torchFactory, ENTITY entity, WritableRawEntity rawEntity) throws Exception;

    void migrate(MigrationAssistant<ENTITY> assistant, String sourceVersion, String targetVersion) throws Exception;
}
