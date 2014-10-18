package org.brightify.torch.action.save;

import org.brightify.torch.util.async.Callback;

import java.util.Map;

/**
 * Asynchronous variant of a save action. Calling any of its methods will require you to pass in a callback to be
 * notified of the result. It will not block the current thread.
 *
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface AsyncSaver {

    /**
     * Asynchronously save a single entity. This will also set the ID of the entity, if it was previously null.
     *
     * @param callback Instance of a callback to be called after the operation is completed.
     * @param entity   Entity to be saved.
     * @param <ENTITY> Type of the saved entity.
     */
    <ENTITY> void entity(Callback<Long> callback, ENTITY entity);

    /**
     * Asynchronously save multiple entities entered as varargs. It will also set ID for entities that had null ID
     * before save.
     *
     * @param callback Instance of a callback to be called after the operation is completed.
     * @param entities Entities to be saved.
     * @param <ENTITY> Type of saved entities.
     */
    <ENTITY> void entities(Callback<Map<ENTITY, Long>> callback, ENTITY... entities);

    /**
     * Asynchronously save multiple entities entered as any iterable. It will also set ID for entities tha had null ID
     * before save.
     *
     * @param callback Instance of a callback to be called after the operation is completed.
     * @param entities Entities to be saved.
     * @param <ENTITY> Type of saved entities.
     */
    <ENTITY> void entities(Callback<Map<ENTITY, Long>> callback, Iterable<ENTITY> entities);

}
