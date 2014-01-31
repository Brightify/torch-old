package org.brightify.torch.action.save;

import org.brightify.torch.Torch;
import org.brightify.torch.Key;
import org.brightify.torch.Result;
import org.brightify.torch.util.Callback;
import org.brightify.torch.util.ResultWrapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class SaverImpl implements Saver {

    protected final Torch mTorch;

    public SaverImpl(Torch torch) {
        mTorch = torch;
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



        return new SaveResultImpl<ENTITY>(mTorch, entities);
    }

}
