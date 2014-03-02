package org.brightify.torch.util;

import org.brightify.torch.EntityMetadata;
import org.brightify.torch.TorchService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom implementation of {@link ArrayList} which doesn't load all
 *
 * @param <ENTITY> type of entity being handled
 */
class LazyArrayList<ENTITY> extends ArrayList<ENTITY> {

    private final EntityMetadata<ENTITY> entityMetadata;
    private final Map<Integer, Long> ids = new HashMap<Integer, Long>();
    private final Map<Integer, Boolean> loaded = new HashMap<Integer, Boolean>();

    public LazyArrayList(EntityMetadata<ENTITY> entityMetadata, Long... ids) {
        this(entityMetadata, Arrays.asList(ids));
    }

    public LazyArrayList(EntityMetadata<ENTITY> entityMetadata, List<Long> ids) {
        super(ids.size());
        this.entityMetadata = entityMetadata;
        for (Long id : ids) {
            this.ids.put(super.size(), id);
            loaded.put(super.size(), false);
            super.add(null);
        }
    }

    public ENTITY get(int i) {
        ENTITY object = super.get(i);
        if (object != null) {
            return object;
        }

        return loadIfNeeded(i);
    }

    @Override
    public ENTITY set(int i, ENTITY t) {
        loadIfNeeded(i);
        return super.set(i, t);
    }

    @Override
    public boolean contains(Object o) {
        if (!entityMetadata.getEntityClass().isAssignableFrom(o.getClass())) {
            return false;
        }
        ENTITY castObject = (ENTITY) o;
        Long id = entityMetadata.getEntityId(castObject);
        if (id != null) {
            Integer count = TorchService.torch().load().type(entityMetadata.getEntityClass()).filter(entityMetadata
                    .getIdColumn().equalTo(id)).count();
            if (count == 1) {
                return true;
            }
        }
        return super.contains(o);

    }

    /**
     * @param i index of item to load
     * @return instance of T if load was needed, null if value was already cached
     */
    public ENTITY loadIfNeeded(int i) {
        Boolean loaded = this.loaded.get(i);
        Long id = ids.get(i);
        if (loaded != null && !loaded && id != null) {
            ENTITY object = TorchService.torch().load().type(entityMetadata.getEntityClass()).id(id);
            this.loaded.put(i, true);
            super.set(i, object);
            return object;
        } else {
            return null;
        }
    }

    public void loadAll() {
        for (Integer index : ids.keySet()) {
            loadIfNeeded(index);
        }
    }
}
