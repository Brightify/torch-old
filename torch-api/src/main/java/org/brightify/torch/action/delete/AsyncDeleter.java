package org.brightify.torch.action.delete;

import org.brightify.torch.Key;
import org.brightify.torch.util.Callback;

import java.util.Map;

/**
 * Asynchronous variant of a delete action. Calling any of its methods will require you to pass in a callback to be
 * notified of the result. It will not block the current thread.
 *
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface AsyncDeleter {

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
    <ENTITY> void entities(Callback<Map<Key<ENTITY>, Boolean>> callback, ENTITY... entities);

    /**
     * Asynchronously delete multiple entities entered as any iterable.
     *
     * @param callback Instance of a callback to be called after the operation is completed.
     * @param entities Entities to be deleted.
     * @param <ENTITY> Type of the deleted entity.
     */
    <ENTITY> void entities(Callback<Map<Key<ENTITY>, Boolean>> callback, Iterable<ENTITY> entities);

    /**
     * Asynchronously delete entity by its key. This might be useful when you have only the ID of the netity and you
     * don't want to waste time by loading the entity.
     *
     * @param callback Instance of a callback to be called after the operation is completed.
     * @param key      Key of entities to be deleted.
     * @param <ENTITY> Type of deleted entities.
     */
    <ENTITY> void key(Callback<Boolean> callback, Key<ENTITY> key);

    /**
     * Asynchronously delete entities by their keys, entered as varargs.
     *
     * @param callback Instance of a callback to be called after the operation is completed.
     * @param keys     Keys of entities to be deleted.
     * @param <ENTITY> Type of deleted entities.
     */
    <ENTITY> void keys(Callback<Map<Key<ENTITY>, Boolean>> callback, Key<ENTITY>... keys);

    /**
     * Asynchronously delete entities by their keys, entered as any iterable.
     *
     * @param callback Instance of a callback to be called after the operation is completed.
     * @param keys     Keys of entities to be deleted.
     * @param <ENTITY> Type of deleted entities.
     */
    <ENTITY> void keys(Callback<Map<Key<ENTITY>, Boolean>> callback, Iterable<Key<ENTITY>> keys);

}
