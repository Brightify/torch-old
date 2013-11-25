package com.brightgestures.brightify.action.save;

import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Result;
import com.brightgestures.brightify.util.Callback;
import com.brightgestures.brightify.util.ResultWrapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class SaverImpl implements Saver {

    protected final Brightify mBrightify;

    public SaverImpl(Brightify brightify) {
        mBrightify = brightify;
    }

    @Override
    public <ENTITY> Result<Key<ENTITY>> entity(ENTITY entity) {
        Result<Map<Key<ENTITY>, ENTITY>> base = entities(Collections.singleton(entity));

        return new ResultWrapper<Map<Key<ENTITY>, ENTITY>, Key<ENTITY>>(base) {
            @Override
            protected Key<ENTITY> wrap(Map<Key<ENTITY>, ENTITY> original) {
                return original.keySet().iterator().next();
            }

            @Override
            public void async(Callback<Key<ENTITY>> callback) {
                throw new UnsupportedOperationException("Not implemented!");
            }
        };
    }

    @Override
    public <ENTITY> Result<Map<Key<ENTITY>, ENTITY>> entities(ENTITY... entities) {
        return entities(Arrays.asList(entities));
    }

    @Override
    public <ENTITY> Result<Map<Key<ENTITY>, ENTITY>> entities(Collection<ENTITY> entities) {



        return new SaveResultImpl<ENTITY>(mBrightify, entities);
    }

}
