package org.brightify.torch.action.delete;

import org.brightify.torch.EntityDescription;
import org.brightify.torch.Key;
import org.brightify.torch.Torch;
import org.brightify.torch.util.async.AsyncRunner;
import org.brightify.torch.util.async.Callback;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class DeleterImpl implements Deleter, AsyncDeleter {

    protected final Torch torch;

    public DeleterImpl(Torch torch) {
        this.torch = torch;
    }

    @Override
    public AsyncDeleter async() {
        return this;
    }

    @Override
    public <ENTITY> Boolean entity(ENTITY entity) {
        return entities(Collections.singleton(entity)).values().iterator().next();
    }

    @Override
    public <ENTITY> Map<ENTITY, Boolean> entities(ENTITY... entities) {
        return entities(Arrays.asList(entities));
    }

    @Override
    public <ENTITY> Map<ENTITY, Boolean> entities(Iterable<ENTITY> entities) {
        if (entities == null) {
            throw new IllegalArgumentException("Entities cannot be null!");
        }

        return torch.getFactory().getDatabaseEngine().delete(entities);
    }

    @Override
    public <ENTITY> void entity(Callback<Boolean> callback, final ENTITY entity) {
        AsyncRunner.submit(callback, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return entity(entity);
            }
        });
    }

    @Override
    public <ENTITY> void entities(Callback<Map<ENTITY, Boolean>> callback, final ENTITY... entities) {
        AsyncRunner.submit(callback, new Callable<Map<ENTITY, Boolean>>() {
            @Override
            public Map<ENTITY, Boolean> call() throws Exception {
                return entities(entities);
            }
        });
    }

    @Override
    public <ENTITY> void entities(Callback<Map<ENTITY, Boolean>> callback, final Iterable<ENTITY> entities) {
        AsyncRunner.submit(callback, new Callable<Map<ENTITY, Boolean>>() {
            @Override
            public Map<ENTITY, Boolean> call() throws Exception {
                return entities(entities);
            }
        });
    }

}
