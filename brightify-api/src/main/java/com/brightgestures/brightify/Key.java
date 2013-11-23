package com.brightgestures.brightify;

import java.io.Serializable;

/**
 * Key is unique definition of an entity and should be used instead of using plain entity id.
 *
 * @param <ENTITY>
 */
public class Key<ENTITY> implements Serializable {

    private final long mId;
    private final Class<ENTITY> mType;

    Key(Class<ENTITY> entityClass, long id) {
        mId = id;
        mType = entityClass;
    }

    public long getId() {
        return mId;
    }

    public Class<ENTITY> getType() {
        return mType;
    }
}
