package com.brightgestures.brightify.action.delete;

import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.*;
import com.brightgestures.brightify.util.Callback;
import com.brightgestures.brightify.util.ResultWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class DeleterImpl<ENTITY> implements Deleter, TypedDeleter<ENTITY> {

    protected final Brightify mBrightify;
    protected final DeleterImpl<?> mPreviousDeleter;
    protected final DeleterType<ENTITY> mDeleterType;

    public DeleterImpl(Brightify brightify) {
        this(brightify, null, new DeleterType.NopDeleterType<ENTITY>());
    }

    public DeleterImpl(Brightify brightify, DeleterImpl<?> previousDeleter, DeleterType<ENTITY> deleterType) {
        mBrightify = brightify;
        mPreviousDeleter = previousDeleter;
        mDeleterType = deleterType;
    }

    @Override
    public <ENTITY> Result<Boolean> entity(ENTITY entity) {
        Class<ENTITY> entityClass = (Class<ENTITY>) entity.getClass();
        EntityMetadata<ENTITY> metadata = mBrightify.getFactory().getEntities().getMetadata(entityClass);

        return type(entityClass).id(metadata.getEntityId(entity));
    }

    @Override
    public <ENTITY> Result<Map<Key<ENTITY>, Boolean>> entities(ENTITY... entities) {
        return entities(Arrays.asList(entities));
    }

    @Override
    public <ENTITY> Result<Map<Key<ENTITY>, Boolean>> entities(Collection<ENTITY> entities) {
        if(entities == null || entities.size() == 0) {
            throw new IllegalArgumentException("There has to be at least one entity!");
        }

        Class<ENTITY> entityClass = (Class<ENTITY>) entities.iterator().next().getClass();
        EntityMetadata<ENTITY> metadata = mBrightify.getFactory().getEntities().getMetadata(entityClass);
        LinkedList<Long> ids = new LinkedList<Long>();
        for(ENTITY entity : entities) {
            ids.addLast(metadata.getEntityId(entity));
        }

        return type(entityClass).ids(ids);
    }

    @Override
    public <ENTITY> Result<Boolean> key(Key<ENTITY> key) {
        Result<Map<Key<ENTITY>, Boolean>> base = keys(Collections.singleton(key));

        return new ResultWrapper<Map<Key<ENTITY>, Boolean>, Boolean>(base) {
            @Override
            protected Boolean wrap(Map<Key<ENTITY>, Boolean> original) {
                return original.values().iterator().next();
            }

            @Override
            public void async(Callback<Boolean> callback) {
                throw new UnsupportedOperationException("Not implemented!");
            }
        };
    }

    @Override
    public <ENTITY> Result<Map<Key<ENTITY>, Boolean>> keys(Key<ENTITY>... keys) {
        return keys(Arrays.asList(keys));
    }

    @Override
    public <ENTITY> Result<Map<Key<ENTITY>, Boolean>> keys(Collection<Key<ENTITY>> keys) {
        if(keys == null || keys.size() == 0) {
            throw new IllegalArgumentException("There has to be at least one key!");
        }

        return new DeleteResultImpl<ENTITY>(mBrightify, keys);
    }

    @Override
    public <ENTITY> TypedDeleter<ENTITY> type(Class<ENTITY> entityClass) {
        return new DeleterImpl<ENTITY>(mBrightify, this, new DeleterType.TypedDeleterType<ENTITY>(entityClass));
    }

    @Override
    public Result<Boolean> id(long id) {
        Result<Map<Key<ENTITY>, Boolean>> base = ids(Collections.singleton(id));

        return new ResultWrapper<Map<Key<ENTITY>, Boolean>, Boolean>(base) {
            @Override
            protected Boolean wrap(Map<Key<ENTITY>, Boolean> original) {
                return original.values().iterator().next();
            }

            @Override
            public void async(Callback<Boolean> callback) {
                throw new UnsupportedOperationException("Not implemented!");
            }
        };
    }

    @Override
    public Result<Map<Key<ENTITY>, Boolean>> ids(Long... ids) {
        return ids(Arrays.asList(ids));
    }

    @Override
    public Result<Map<Key<ENTITY>, Boolean>> ids(Collection<Long> ids) {
        if(ids == null || ids.size() == 0) {
            throw new IllegalArgumentException("There has to be at least one id!");
        }

        // TODO I don't like the cast here, it's based on presumption that "ids" comes only after "type" which is true now, but might change in future
        DeleterType.TypedDeleterType<ENTITY> typedDeleterType = (DeleterType.TypedDeleterType<ENTITY>) mDeleterType;

        LinkedList<Key<ENTITY>> keys = new LinkedList<Key<ENTITY>>();
        for(long id : ids) {
            Key<ENTITY> key = KeyFactory.create(typedDeleterType.mEntityClass, id);

            keys.addLast(key);
        }

        return new DeleteResultImpl<ENTITY>(mBrightify, keys);
    }
}
