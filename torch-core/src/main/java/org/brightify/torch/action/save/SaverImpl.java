package org.brightify.torch.action.save;

import org.brightify.torch.Torch;
import org.brightify.torch.util.async.AsyncRunner;
import org.brightify.torch.util.async.Callback;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

public class SaverImpl implements Saver {

    private final Torch torch;

    public SaverImpl(Torch torch) {
        this.torch = torch;
    }

    @Override
    public <ENTITY> Long entity(ENTITY entity) {
        return entities(Collections.singleton(entity)).values().iterator().next();
    }

    @Override
    public <ENTITY> Map<ENTITY, Long> entities(ENTITY... entities) {
        return entities(Arrays.asList(entities));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <ENTITY> Map<ENTITY, Long> entities(Iterable<ENTITY> entities) {
        return torch.getFactory().getDatabaseEngine().save(entities);
    }

    @Override
    public <ENTITY> void entity(Callback<Long> callback, final ENTITY entity) {
        AsyncRunner.submit(callback, new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return entity(entity);
            }
        });
    }

    @Override
    public <ENTITY> void entities(Callback<Map<ENTITY, Long>> callback, final ENTITY... entities) {
        AsyncRunner.submit(callback, new Callable<Map<ENTITY, Long>>() {
            @Override
            public Map<ENTITY, Long> call() throws Exception {
                return entities(entities);
            }
        });
    }

    @Override
    public <ENTITY> void entities(Callback<Map<ENTITY, Long>> callback, final Iterable<ENTITY> entities) {
        AsyncRunner.submit(callback, new Callable<Map<ENTITY, Long>>() {
            @Override
            public Map<ENTITY, Long> call() throws Exception {
                return entities(entities);
            }
        });
    }
}
