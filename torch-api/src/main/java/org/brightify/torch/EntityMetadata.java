package org.brightify.torch;

import org.brightify.torch.annotation.Entity;
import org.brightify.torch.filter.NumberColumn;
import org.brightify.torch.util.MigrationAssistant;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface EntityMetadata<ENTITY> {
    NumberColumn<Long> getIdColumn();

    String[] getColumns();

    String getTableName();

    String getVersion();

    Entity.MigrationType getMigrationType();

    Long getEntityId(ENTITY entity);

    void setEntityId(ENTITY entity, Long id);

    Class<ENTITY> getEntityClass();

    Key<ENTITY> createKey(ENTITY entity);

    EntityDescription describeEntity();

    ENTITY createFromRawEntity(RawEntity cursor) throws Exception;

    void toRawEntity(ENTITY entity, RawEntity rawEntity) throws Exception;

    void migrate(MigrationAssistant<ENTITY> assistant, String sourceVersion, String targetVersion) throws Exception;
}
