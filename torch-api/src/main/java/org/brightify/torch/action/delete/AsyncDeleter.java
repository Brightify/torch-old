package org.brightify.torch.action.delete;

import org.brightify.torch.util.async.Callback;

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
