package org.brightify.torch.android.internal;

import org.brightify.torch.EntityDescription;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.filter.NumberProperty;
import org.brightify.torch.filter.Property;
import org.brightify.torch.filter.ReferenceProperty;
import org.brightify.torch.filter.StringProperty;
import org.brightify.torch.filter.ValueProperty;
import org.brightify.torch.impl.filter.IntegerPropertyImpl;
import org.brightify.torch.impl.filter.StringPropertyImpl;
import org.brightify.torch.util.ArrayListBuilder;
import org.brightify.torch.util.MigrationAssistant;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class SQLiteMaster$ implements EntityDescription<SQLiteMaster> {

    public static final StringProperty<SQLiteMaster> type =
            new StringPropertyImpl<SQLiteMaster>(SQLiteMaster.class, "type", "type") {
                @Override
                public String get(SQLiteMaster owner) {
                    return owner.getType();
                }

                @Override
                public void set(SQLiteMaster owner, String value) {
                    owner.setType(value);
                }
            };
    public static final StringProperty<SQLiteMaster> name =
            new StringPropertyImpl<SQLiteMaster>(SQLiteMaster.class, "name", "name") {
                @Override
                public String get(SQLiteMaster owner) {
                    return owner.getName();
                }

                @Override
                public void set(SQLiteMaster owner, String value) {
                    owner.setName(value);
                }
            };
    public static final StringProperty<SQLiteMaster> tableName =
            new StringPropertyImpl<SQLiteMaster>(SQLiteMaster.class, "tbl_name", "tbl_name") {
                @Override
                public String get(SQLiteMaster owner) {
                    return owner.getTableName();
                }

                @Override
                public void set(SQLiteMaster owner, String value) {
                    owner.setTableName(value);
                }
            };
    public static final NumberProperty<SQLiteMaster, Integer> rootpage =
            new IntegerPropertyImpl<SQLiteMaster>(SQLiteMaster.class, "rootpage", "rootpage") {
                @Override
                public Integer get(SQLiteMaster owner) {
                    return owner.getRootpage();
                }

                @Override
                public void set(SQLiteMaster owner, Integer value) {
                    owner.setRootpage(value);
                }
            };
    public static final StringProperty<SQLiteMaster> sql =
            new StringPropertyImpl<SQLiteMaster>(SQLiteMaster.class, "sql", "sql") {
                @Override
                public String get(SQLiteMaster owner) {
                    return owner.getSql();
                }

                @Override
                public void set(SQLiteMaster owner, String value) {
                    owner.setSql(value);
                }
            };

    private static final List<? extends Property<SQLiteMaster, ?>> properties =
            ArrayListBuilder.<Property<SQLiteMaster, ?>>begin()
                            .add(type)
                            .add(name)
                            .add(tableName)
                            .add(rootpage)
                            .add(sql)
                            .list();

    private static final List<? extends ValueProperty<SQLiteMaster, ?>> valueProperties =
            ArrayListBuilder.<ValueProperty<SQLiteMaster, ?>>begin()
                            .add(type)
                            .add(name)
                            .add(tableName)
                            .add(rootpage)
                            .add(sql)
                            .list();

    private static final List<? extends ReferenceProperty<SQLiteMaster, ?>> referenceProperties =
            ArrayListBuilder.<ReferenceProperty<SQLiteMaster, ?>>begin().list();

    public static SQLiteMaster$ create() {
        return new SQLiteMaster$();
    }

    @Override
    public NumberProperty<SQLiteMaster, Long> getIdProperty() {
        return null;
    }

    @Override
    public List<? extends Property<SQLiteMaster, ?>> getProperties() {
        return properties;
    }

    @Override
    public List<? extends ValueProperty<SQLiteMaster, ?>> getValueProperties() {
        return valueProperties;
    }

    @Override
    public List<? extends ReferenceProperty<SQLiteMaster, ?>> getReferenceProperties() {
        return referenceProperties;
    }

    @Override
    public String getSafeName() {
        return "sqlite_master";
    }

    @Override
    public long getRevision() {
        return 1;
    }

    @Override
    public Entity.MigrationType getMigrationType() {
        return Entity.MigrationType.MIGRATE;
    }

    @Override
    public Class<SQLiteMaster> getEntityClass() {
        return SQLiteMaster.class;
    }

    @Override
    public SQLiteMaster createEmpty() {
        return new SQLiteMaster();
    }

    @Override
    public void migrate(MigrationAssistant<SQLiteMaster> assistant, long sourceRevision, long targetRevision)
            throws Exception {
    }
}
