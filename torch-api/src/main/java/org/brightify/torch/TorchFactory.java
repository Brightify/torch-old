package org.brightify.torch;

import org.brightify.torch.relation.RelationResolver;

/**
 * This class maintains the communication between the {@link DatabaseEngine} and actors. Instances of {@link Torch}
 * spawned by the factory will be able to operate with entities registered in this factory. If you need to have more
 * than one separate database, you can create your own factory. If you create more factories with the same {@link
 * DatabaseEngine} at the same time, the behavior is unknown at this moment.
 *
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface TorchFactory {

    DatabaseEngine getDatabaseEngine();

    /**
     * Spawns new instance of {@link Torch}. You should not keep the instance.
     *
     * @return New instance of Torch.
     */
    Torch begin();

    /**
     * Unloads this factory. Use this when this instance should get out of scope.
     */
    void unload();

    Entities getEntities();

    /**
     * @return An instance of a relation resolver.
     */
    RelationResolver getRelationResolver();

}