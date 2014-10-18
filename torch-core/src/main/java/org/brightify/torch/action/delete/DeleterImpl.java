package org.brightify.torch.action.delete;

import org.brightify.torch.EntityDescription;
import org.brightify.torch.Key;
import org.brightify.torch.Torch;
import org.brightify.torch.util.AsyncRunner;
import org.brightify.torch.util.Callback;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

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
    public <ENTITY> Map<Key<ENTITY>, Boolean> entities(ENTITY... entities) {
        return entities(Arrays.asList(entities));
    }

    @Override
    public <ENTITY> Map<Key<ENTITY>, Boolean> entities(Iterable<ENTITY> entities) {
        if (entities == null) {
            throw new IllegalArgumentException("Entities cannot be null!");
        }

        EntityDescription<ENTITY> metadata = null;
        LinkedList<Key<ENTITY>> keys = new LinkedList<Key<ENTITY>>();
        for (ENTITY entity : entities) {
            if (metadata == null) {
                metadata = torch.getFactory().getEntities().getDescription((Class<ENTITY>) entity.getClass());
            }
            keys.addLast(metadata.createKey(entity));
        }

        return keys(keys);
    }

    @Override
    public <ENTITY> Boolean key(Key<ENTITY> key) {
        return keys(Collections.singleton(key)).values().iterator().next();
    }

    @Override
    public <ENTITY> Map<Key<ENTITY>, Boolean> keys(Key<ENTITY>... keys) {
        return keys(Arrays.asList(keys));
    }

    @Override
    public <ENTITY> Map<Key<ENTITY>, Boolean> keys(Iterable<Key<ENTITY>> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Keys cannot be null!");
        }

        return torch.getFactory().getDatabaseEngine().delete(keys);
    }

    @Override
    public <ENTITY> void entity(Callback<Boolean> callback, final ENTITY entity) {
        AsyncRunner.submit(callback, new AsyncRunner.Task<Boolean>() {
            @Override
            public Boolean doWork() throws Exception {
                return entity(entity);
            }
        });
    }

    @Override
    public <ENTITY> void entities(Callback<Map<Key<ENTITY>, Boolean>> callback, final ENTITY... entities) {
        AsyncRunner.submit(callback, new AsyncRunner.Task<Map<Key<ENTITY>, Boolean>>() {
            @Override
            public Map<Key<ENTITY>, Boolean> doWork() throws Exception {
                return entities(entities);
            }
        });
    }

    @Override
    public <ENTITY> void entities(Callback<Map<Key<ENTITY>, Boolean>> callback, final Iterable<ENTITY> entities) {
        AsyncRunner.submit(callback, new AsyncRunner.Task<Map<Key<ENTITY>, Boolean>>() {
            @Override
            public Map<Key<ENTITY>, Boolean> doWork() throws Exception {
                return entities(entities);
            }
        });
    }

    @Override
    public <ENTITY> void key(Callback<Boolean> callback, final Key<ENTITY> key) {
        AsyncRunner.submit(callback, new AsyncRunner.Task<Boolean>() {
            @Override
            public Boolean doWork() throws Exception {
                return key(key);
            }
        });
    }

    @Override
    public <ENTITY> void keys(Callback<Map<Key<ENTITY>, Boolean>> callback, final Key<ENTITY>... keys) {
        AsyncRunner.submit(callback, new AsyncRunner.Task<Map<Key<ENTITY>, Boolean>>() {
            @Override
            public Map<Key<ENTITY>, Boolean> doWork() throws Exception {
                return keys(keys);
            }
        });
    }

    @Override
    public <ENTITY> void keys(Callback<Map<Key<ENTITY>, Boolean>> callback, final Iterable<Key<ENTITY>> keys) {
        AsyncRunner.submit(callback, new AsyncRunner.Task<Map<Key<ENTITY>, Boolean>>() {
            @Override
            public Map<Key<ENTITY>, Boolean> doWork() throws Exception {
                return keys(keys);
            }
        });
    }
}
