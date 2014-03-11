package org.brightify.torch.action.delete;

import org.brightify.torch.Key;
import org.brightify.torch.action.AsyncSelector;

import java.util.Map;

/**
 * Synchronous variant of a delete action. Calling any of its methods will begin the delete operation immediately on the
 * current thread, blocking it until the operation is done.
 *
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Deleter extends AsyncSelector<AsyncDeleter> {

    /**
     * Delete a single entity.
     *
     * @param entity   Entity to be deleted.
     * @param <ENTITY> Type of the deleted entity.
     *
     * @return True if the delete was successful.
     */
    <ENTITY> Boolean entity(ENTITY entity);

    /**
     * Delete multiple entities entered as varargs.
     *
     * @param entities Entities to be deleted.
     * @param <ENTITY> Type of deleted entities.
     *
     * @return Map of keys for each entity with value set to true if delete was successful for the entity.
     */
    <ENTITY> Map<Key<ENTITY>, Boolean> entities(ENTITY... entities);

    /**
     * Delete multiple entities entered as any iterable.
     *
     * @param entities Entities to be deleted.
     * @param <ENTITY> Type of deleted entities.
     *
     * @return Map of keys for each entity with value set to true if delete was successful for the entity.
     */
    <ENTITY> Map<Key<ENTITY>, Boolean> entities(Iterable<ENTITY> entities);

    /**
     * Delete entity by its key. This might be useful when you have only the ID of the entity and you don't want to
     * waste time by loading the entity.
     *
     * @param key      Key of an entity to be deleted.
     * @param <ENTITY> Type of the deleted entity.
     *
     * @return True if the delete was successful.
     */
    <ENTITY> Boolean key(Key<ENTITY> key);

    /**
     * Delete multiple entities by their keys, entered as varargs.
     *
     * @param keys     Keys of entities to be deleted.
     * @param <ENTITY> Type of deleted entities.
     *
     * @return Map of keys for each entity with value set to true if delete was successful for the key.
     */
    <ENTITY> Map<Key<ENTITY>, Boolean> keys(Key<ENTITY>... keys);

    /**
     * Delete multiple entities by their keys, entered as any iterable.
     *
     * @param keys     Keys of entities to be deleted.
     * @param <ENTITY> Type of deleted entities.
     *
     * @return Map of keys for each entity with value set to true if delete was successful for the key.
     */
    <ENTITY> Map<Key<ENTITY>, Boolean> keys(Iterable<Key<ENTITY>> keys);

}
