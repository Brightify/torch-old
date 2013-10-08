package com.brightgestures.brightify.action;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.Entities;
import com.brightgestures.brightify.EntityMetadata;
import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Property;
import com.brightgestures.brightify.Result;
import com.brightgestures.brightify.action.load.FilterLoaderImpl;
import com.brightgestures.brightify.action.load.filter.Closeable;
import com.brightgestures.brightify.action.load.filter.Filterable;
import com.brightgestures.brightify.action.load.filter.Nestable;
import com.brightgestures.brightify.action.load.filter.OperatorFilter;
import com.brightgestures.brightify.util.ResultWrapper;
import com.brightgestures.brightify.util.Serializer;
import com.brightgestures.brightify.util.TypeUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Loader {

    public static class BaseLoader<E extends BaseLoader> {

    }

    public static abstract class GroupLoaderImpl<E, L extends GroupLoader> implements GroupLoader<E, L> {
        private List<Class<?>> mGroups = new ArrayList<Class<?>>();

        @Override
        public L group(Class<?> group) {
            return groups(group);
        }

        @Override
        public L groups(Class<?>... groups) {
            Collections.addAll(mGroups, groups);
            return (L) this;
        }
    }

    public static class InitialLoaderImpl extends GroupLoaderImpl<Object, InitialLoader> implements InitialLoader {
        private List<Class<?>> mGroups = new ArrayList<Class<?>>();

        @Override
        public <E> Result<E> key(Key<E> key) {
            return type(key.getType()).id(key.getId());
        }

        @Override
        public <E> TypedLoader<E> type(Class<E> type) {
            return new TypedLoaderImpl<E>(type, mGroups);
        }
    }

    public static class TypedLoaderImpl<E> extends GroupLoaderImpl<E, TypedLoader<E>> implements TypedLoader<E>, Filterable<E> {
        private final List<Class<?>> mGroups;
        private final Class<E> mType;

        public TypedLoaderImpl(Class<E> type, List<Class<?>> groups) {
            mType = type;
            mGroups = groups;
        }

        @Override
        public Result<E> id(Long id) {
            Result<List<E>> base = ids(id);

            return new ResultWrapper<List<E>, E>(base) {
                @Override
                protected E wrap(List<E> original) {
                    return original.iterator().next();
                }
            };
        }

        @Override
        public <T extends ListLoader<E> & Closeable & OperatorFilter> Result<List<E>> ids(Long... ids) {
            // create filter and add all ids
            String columnName = Entities.getMetadata(mType).getIdProperty().getColumnName();
            String condition = columnName + "=";

            T filter = null;
            for(Long id : ids) {
                if(filter == null) {
                    filter = filter(condition, id);
                    continue;
                }
                filter = filter.or(condition, id);
            }

            return null;
        }

        @Override
        public <T extends ListLoader<E> & Closeable & OperatorFilter> T filter(String condition, Object value) {
            return (T) new FilterLoaderImpl<E>(mType, mGroups).filter(condition, value);
        }
    }

    public interface InitialLoader extends GroupLoader<Object, InitialLoader> {
        <E> Result<E> key(Key<E> key);

        <E> TypedLoader<E> type(Class<E> type);
    }

    public interface GroupLoader<E, L extends GroupLoader> {
        L group(Class<?> group);

        L groups(Class<?>... groups);
    }

    public interface TypedLoader<E> extends GroupLoader<E, TypedLoader<E>> {
        Result<E> id(Long id);

        <T extends ListLoader<E> & Closeable & OperatorFilter> Result<List<E>> ids(Long... ids);
    }

    public interface FilteredLoader<E, L extends FilteredLoader> {
        L filter(String condition, String value);
    }

    public interface ListLoader<E> extends Iterable<E> {
        List<E> list();
    }


    private Brightify mBrightify;

    protected Set<Class<?>> mLoadGroups;

    public Loader(Brightify brightify) {
        mBrightify = brightify;
        mLoadGroups = Collections.emptySet();
    }

    public Loader group(Class<?>... groups) {
        mLoadGroups.addAll(Arrays.asList(groups));

        return this;
    }

    public <E> LoadType<E> type(Class<E> type) {
        return new LoadType<E>(this, type);
    }

    public <E> LoadResult<E> key(Key<E> key) {
        return new LoadResult<E>(key);
    }

    public <E> LoadResult<E> entity(E entity) {
        return key(Key.create(entity));
    }

    public <E> E now(Key<E> key) {
        return key(key).now();
    }

    public class LoadType<E> {
        private Loader mLoader;
        private Class<E> mType;

        public LoadType(Loader loader, Class<E> type) {
            mLoader = loader;
            mType = type;
        }


        public LoadType<E> filter(String condition, Object value) {
            // TODO implement
            return this;
        }

        public LoadResult<E> id(Long id) {
            return mLoader.key(Key.create(mType, id));
        }

        public Map<Long, E> ids(Long... ids) {
            for(Long id : ids) {

            }
            return null; // TODO implement! https://code.google.com/p/objectify-appengine/source/browse/src/main/java/com/googlecode/objectify/impl/LoadTypeImpl.java
        }
    }

    public class LoadResult<E> implements Result<List<E>> {

        private final Key<E> mKey;

        public LoadResult(Key<E> key) {
            mKey = key;
        }

        @Override
        public List<E> now() {
            SQLiteDatabase db = mBrightify.getReadableDatabase();
            Cursor cursor = null;
            try {
                EntityMetadata<E> metadata = Entities.getMetadataByKey(mKey);

                int columnsCount = metadata.getProperties().size();

                String[] columns = new String[columnsCount];

                int i = 0;
                for(Property property : metadata.getProperties()) {
                    columns[i] = property.getColumnName();
                    i++;
                }

                //db.query(distinct, table from type, columns, selectionstring, )

                cursor = db.query(metadata.getTableName(), columns, null, null, null, null, null);

                ArrayList<E> entities = new ArrayList<E>();

                if(cursor == null || !cursor.moveToFirst()) {
                    return entities;
                }

                do {
                    E entity = createFromCursor(metadata, cursor);

                    entities.add(entity);
                } while (cursor.moveToNext());

                return entities;
            } finally {
                if(cursor != null) {
                    cursor.close();
                }
                db.close();
            }
        }

        @SuppressWarnings("unchecked")
        private E createFromCursor(EntityMetadata<E> metadata, Cursor cursor) {
            E entity = (E) TypeUtils.construct(metadata.getEntityClass());

            for(Property property : metadata.getProperties()) {
                int index = cursor.getColumnIndex(property.getColumnName());
                Class<?> type = property.getType();
                Object value = null;
                if(TypeUtils.isAssignableFrom(Boolean.class, type)) {
                    value = cursor.getInt(index) > 0;
                } else if(TypeUtils.isAssignableFrom(Byte.class, type)) {
                    value = (byte) cursor.getInt(index);
                } else if(TypeUtils.isAssignableFrom(Short.class, type)) {
                    value = cursor.getShort(index);
                } else if(TypeUtils.isAssignableFrom(Integer.class, type)) {
                    value = cursor.getInt(index);
                } else if(TypeUtils.isAssignableFrom(Long.class, type)) {
                    value = cursor.getLong(index);
                } else if(TypeUtils.isAssignableFrom(Float.class, type)) {
                    value = cursor.getFloat(index);
                } else if(TypeUtils.isAssignableFrom(Double.class, type)) {
                    value = cursor.getDouble(index);
                } else if(TypeUtils.isAssignableFrom(String.class, type)) {
                    value = cursor.getString(index);
                } else if(TypeUtils.isAssignableFrom(byte[].class, type)) {
                    value = cursor.getBlob(index);
                } else if(TypeUtils.isAssignableFrom(Serializable.class, type)) {
                    try {
                        value = Serializer.deserialize(cursor.getBlob(index));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new IllegalStateException("Type '" + type.toString() + "' cannot be restored from database!");
                }

                property.set(entity, value);
            }

            return entity;
        }
    }

}
