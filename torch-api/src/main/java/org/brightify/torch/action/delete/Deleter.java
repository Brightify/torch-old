package org.brightify.torch.action.delete;

import org.brightify.torch.util.async.Callback;

import java.util.Map;

/**
 * Synchronous variant of a delete action. Calling any of its methods will begin the delete operation immediately on the
 * current thread, blocking it until the operation is done.
 *
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Deleter {

    /**
     * Delete a single entity.
     *
     * @param entity   Entity to be deleted.
     * @param <ENTITY> Type of the deleted entity.
     * @return True if the delete was successful.
     */
    <ENTITY> Boolean entity(ENTITY entity);

    /**
     * Delete multiple entities entered as varargs.
     *
     * @param entities Entities to be deleted.
     * @param <ENTITY> Type of deleted entities.
     * @return Map of keys for each entity with value set to true if delete was successful for the entity.
     */
    <ENTITY> Map<ENTITY, Boolean> entities(ENTITY... entities);

    /**
     * Delete multiple entities entered as any iterable.
     *
     * @param entities Entities to be deleted.
     * @param <ENTITY> Type of deleted entities.
     * @return Map of keys for each entity with value set to true if delete was successful for the entity.
     */
    <ENTITY> Map<ENTITY, Boolean> entities(Iterable<ENTITY> entities);


    /**
     * Asynchronously delete a single entity.
     *
     * @param callback Instance of a callback to be called after the operation is completed.
     * @param entity   Entity to be deleted.
     * @param <ENTITY> Type of the deleted entity.
     */
    <ENTITY> void entity(Callback<Boolean> callback, ENTITY entity);

    /**
     * Asynchronously delete multiple entities entered as varargs.
     *
     * @param callback Instance of a callback to be called after the operation is completed.
     * @param entities Entities to be deleted.
     * @param <ENTITY> Type of the deleted entity.
     */
    <ENTITY> void entities(Callback<Map<ENTITY, Boolean>> callback, ENTITY... entities);

    /**
     * Asynchronously delete multiple entities entered as any iterable.
     *
     * @param callback Instance of a callback to be called after the operation is completed.
     * @param entities Entities to be deleted.
     * @param <ENTITY> Type of the deleted entity.
     */
    <ENTITY> void entities(Callback<Map<ENTITY, Boolean>> callback, Iterable<ENTITY> entities);

}
