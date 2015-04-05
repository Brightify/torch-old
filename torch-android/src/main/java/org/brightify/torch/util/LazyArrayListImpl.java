package org.brightify.torch.util;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.TorchService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Custom implementation of {@link ArrayList} which doesn't load all
 *
 * @param <ENTITY> type of entity being handled
 */
class LazyArrayListImpl<ENTITY> extends ArrayList<ENTITY> implements LazyArrayList<ENTITY> {

    private final EntityDescription<ENTITY> entityDescription;
    private final SparseArray<Long> ids = new SparseArray<Long>();
    private final SparseBooleanArray loaded = new SparseBooleanArray();

    public LazyArrayListImpl(EntityDescription<ENTITY> entityDescription, Long... ids) {
        this(entityDescription, Arrays.asList(ids));
    }

    public LazyArrayListImpl(EntityDescription<ENTITY> entityDescription, List<Long> ids) {
        super(ids.size());
        this.entityDescription = entityDescription;
        for (Long id : ids) {
            this.ids.put(super.size(), id);
            loaded.put(super.size(), false);
            super.add(null);
        }
    }

    @Override
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
        if (!entityDescription.getEntityClass().isAssignableFrom(o.getClass())) {
            return false;
        }
        ENTITY castObject = (ENTITY) o;
        Long id = entityDescription.getIdProperty().get(castObject);
        if (id != null) {
            Integer count = TorchService.torch().load().type(entityDescription.getEntityClass()).filter(
                    entityDescription
                    .getIdProperty().equalTo(id)).count();
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
    @Override
    public ENTITY loadIfNeeded(int i) {
        Boolean loaded = this.loaded.valueAt(i);
        Long id = ids.valueAt(i);
        if (loaded != null && !loaded && id != null) {
            ENTITY object = TorchService.torch().load().type(entityDescription.getEntityClass()).id(id);
            this.loaded.put(i, true);
            super.set(i, object);
            return object;
        } else {
            return null;
        }
    }

    @Override
    public void loadAll() {
        for (int i = 0; i < ids.size(); i++) {
            loadIfNeeded(i);
        }
    }
}
