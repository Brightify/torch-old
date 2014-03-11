package org.brightify.torch.action.save;

import org.brightify.torch.Key;
import org.brightify.torch.action.AsyncSelector;

import java.util.Map;

/**
 * Synchronous variant of a save action. Calling any of its methods will begin the save operation immediately on the
 * current thread, blocking it until the operation is done.
 *
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Saver extends AsyncSelector<AsyncSaver> {

    /**
     * Save a single entity. This will also set the ID of the entity, if it was previously null.
     *
     * @param entity   Entity to be saved.
     * @param <ENTITY> Type of the saved entity.
     *
     * @return Key of the saved entity.
     */
    <ENTITY> Key<ENTITY> entity(ENTITY entity);

    /**
     * Save multiple entities entered as varargs. It will also set ID for entities that had null ID before save.
     *
     * @param entities Entities to be saved.
     * @param <ENTITY> Type of saved entities.
     *
     * @return Map of keys and entities.
     */
    <ENTITY> Map<Key<ENTITY>, ENTITY> entities(ENTITY... entities);

    /**
     * Save multiple entities entered as any iterable. It will also set ID for entities that had null ID before save.
     * @param entities Entities to be saved.
     * @param <ENTITY> Type of saved entities.
     * @return Map of keys and entities.
     */
    <ENTITY> Map<Key<ENTITY>, ENTITY> entities(Iterable<ENTITY> entities);


}
