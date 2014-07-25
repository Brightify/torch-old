package org.brightify.torch.android.internal;

import org.brightify.torch.EntityDescription;
import org.brightify.torch.Key;
import org.brightify.torch.ReadableRawEntity;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.WritableRawEntity;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.filter.NumberProperty;
import org.brightify.torch.filter.NumberPropertyImpl;
import org.brightify.torch.filter.Property;
import org.brightify.torch.filter.StringProperty;
import org.brightify.torch.filter.StringPropertyImpl;
import org.brightify.torch.util.MigrationAssistant;

import java.util.Set;

/**
* @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
*/
public class SQLiteMaster$ implements EntityDescription<SQLiteMaster> {

    public static final StringProperty type = new StringPropertyImpl("type", "type");
    public static final StringProperty name = new StringPropertyImpl("name", "name");
    public static final StringProperty tableName = new StringPropertyImpl("tbl_name", "tbl_name");
    public static final NumberProperty<Integer> rootpage = new NumberPropertyImpl<Integer>("rootpage", "rootpage", Integer.class);
    public static final StringProperty sql = new StringPropertyImpl("sql", "sql");

    private static final Property<?>[] properties = {
            type,
            name,
            tableName,
            rootpage,
            sql
    };

    @Override
    public NumberProperty<Long> getIdProperty() {
        return null;
    }

    @Override
    public Property<?>[] getProperties() {
        return properties;
    }

    @Override
    public String getSafeName() {
        return "sqlite_master";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public Entity.MigrationType getMigrationType() {
        return Entity.MigrationType.MIGRATE;
    }

    @Override
    public Long getEntityId(SQLiteMaster sqLiteMaster) {
        return null;
    }

    @Override
    public void setEntityId(SQLiteMaster sqLiteMaster, Long id) { }

    @Override
    public Class<SQLiteMaster> getEntityClass() {
        return SQLiteMaster.class;
    }

    @Override
    public Key<SQLiteMaster> createKey(SQLiteMaster sqLiteMaster) {
        return null;
    }

    @Override
    public SQLiteMaster createFromRawEntity(TorchFactory torchFactory, ReadableRawEntity rawEntity,
                                            Set<Class<?>> loadGroups) throws Exception {
        SQLiteMaster entity = new SQLiteMaster();
        entity.setType(rawEntity.getString(type.getName()));
        entity.setName(rawEntity.getString(name.getName()));
        entity.setTableName(rawEntity.getString(tableName.getName()));
        entity.setRootpage(rawEntity.getIntegerPrimitive(rootpage.getName()));
        entity.setSql(rawEntity.getString(sql.getName()));
        return entity;
    }

    @Override
    public void toRawEntity(TorchFactory torchFactory, SQLiteMaster entity, WritableRawEntity rawEntity) throws Exception {
        throw new UnsupportedOperationException("sqlite_master table is not supposed to be edited!");
    }

    @Override
    public void migrate(MigrationAssistant<SQLiteMaster> assistant, String sourceVersion, String targetVersion)
            throws Exception {
    }

    public static SQLiteMaster$ create() {
        return new SQLiteMaster$();
    }
}
