package org.brightify.torch.internal;

import android.content.ContentValues;
import android.database.Cursor;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.Key;
import org.brightify.torch.Torch;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.filter.NumberProperty;
import org.brightify.torch.filter.NumberPropertyImpl;
import org.brightify.torch.filter.StringProperty;
import org.brightify.torch.filter.StringPropertyImpl;
import org.brightify.torch.util.MigrationAssistant;

import java.util.List;

/**
* @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
*/
public class SQLiteMaster$ implements EntityMetadata<SQLiteMaster> {

    public static final StringProperty type = new StringPropertyImpl("type");
    public static final StringProperty name = new StringPropertyImpl("name");
    public static final StringProperty tableName = new StringPropertyImpl("tbl_name");
    public static final NumberProperty<Integer> rootpage = new NumberPropertyImpl<Integer>("rootpage");
    public static final StringProperty sql = new StringPropertyImpl("sql");

    @Override
    public NumberProperty<Long> getIdColumn() {
        return null;
    }

    @Override
    public String[] getColumns() {
        return new String[] {
                "type",
                "name",
                "tbl_name",
                "rootpage",
                "sql"
        };
    }

    @Override
    public String getTableName() {
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
    public void setEntityId(SQLiteMaster sqLiteMaster, Long id) {
    }

    @Override
    public Class<SQLiteMaster> getEntityClass() {
        return SQLiteMaster.class;
    }

    @Override
    public Key<SQLiteMaster> createKey(SQLiteMaster sqLiteMaster) {
        return null;
    }

    @Override
    public void createTable(Torch torch) {
    }

    @Override
    public SQLiteMaster createFromCursor(Torch torch, Cursor cursor, List<Class<?>> loadGroups) throws Exception {
        SQLiteMaster entity = new SQLiteMaster();

        {
            int index = cursor.getColumnIndexOrThrow("type");
            if(cursor.isNull(index)) {
                entity.setType(null);
            } else {
                entity.setType(cursor.getString(index));
            }
        }
        {
            int index = cursor.getColumnIndexOrThrow("name");
            if(cursor.isNull(index)) {
                entity.setName(null);
            } else {
                entity.setName(cursor.getString(index));
            }
        }
        {
            int index = cursor.getColumnIndexOrThrow("tbl_name");
            if(cursor.isNull(index)) {
                entity.setTableName(null);
            } else {
                entity.setTableName(cursor.getString(index));
            }
        }
        {
            int index = cursor.getColumnIndexOrThrow("rootpage");
            if(!cursor.isNull(index)) {
                entity.setRootpage(cursor.getInt(index));
            }
        }
        {
            int index = cursor.getColumnIndexOrThrow("sql");
            if(cursor.isNull(index)) {
                entity.setSql(null);
            } else {
                entity.setSql(cursor.getString(index));
            }
        }

        return entity;
    }

    @Override
    public ContentValues toContentValues(Torch torch, SQLiteMaster entity) throws Exception {
        throw new UnsupportedOperationException("sqlite_master table is not supposed to be edited!");

        /*ContentValues values = new ContentValues();

        values.put("type", entity.getType());
        values.put("name", entity.getName());
        values.put("tbl_name", entity.getTableName());
        values.put("rootpage", entity.getRootpage());
        values.put("sql", entity.getSql());

        return values;*/
    }

    @Override
    public void migrate(MigrationAssistant<SQLiteMaster> assistant, String sourceVersion, String targetVersion)
            throws Exception {
    }

    public static SQLiteMaster$ create() {
        return new SQLiteMaster$();
    }
}
