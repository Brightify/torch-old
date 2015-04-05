package org.brightify.torch;

import org.brightify.torch.annotation.Entity;
import org.brightify.torch.filter.NumberProperty;
import org.brightify.torch.filter.Property;
import org.brightify.torch.filter.ReferenceProperty;
import org.brightify.torch.filter.ValueProperty;
import org.brightify.torch.util.Helper;
import org.brightify.torch.util.MigrationAssistant;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface EntityDescription<ENTITY> {
    /**
     * Returns the ID property.
     *
     * @return ID property of the target entity.
     */
    NumberProperty<ENTITY, Long> getIdProperty();

    /**
     * Returns an array of properties.
     *
     * @return An array of properties in the target entity.
     */
    List<? extends Property<ENTITY, ?>> getProperties();

    List<? extends ValueProperty<ENTITY, ?>> getValueProperties();

    List<? extends ReferenceProperty<ENTITY, ?>> getReferenceProperties();

    // FIXME add "getRefCollectionProperties()"

    /**
     * Returns a safe class name.
     *
     * @see Helper#safeNameFromClass(Class) for meaning of the safety.
     *
     * @return Safe name of the entity.
     */
    String getSafeName();

    long getRevision();

    Entity.MigrationType getMigrationType();

    Class<ENTITY> getEntityClass();

    ENTITY createEmpty();

    void migrate(MigrationAssistant<ENTITY> assistant, long sourceRevision, long targetRevision) throws Exception;

}
