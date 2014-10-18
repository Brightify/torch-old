package org.brightify.torch.action.delete;

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
    <ENTITY> Map<ENTITY, Boolean> entities(ENTITY... entities);

    /**
     * Delete multiple entities entered as any iterable.
     *
     * @param entities Entities to be deleted.
     * @param <ENTITY> Type of deleted entities.
     *
     * @return Map of keys for each entity with value set to true if delete was successful for the entity.
     */
    <ENTITY> Map<ENTITY, Boolean> entities(Iterable<ENTITY> entities);

}
