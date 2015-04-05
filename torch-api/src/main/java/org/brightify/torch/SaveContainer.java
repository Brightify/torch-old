package org.brightify.torch;

import java.util.Set;

public interface SaveContainer {

    Set<Class<?>> getSaveGroups();

    void addReferenceToQueue(Ref<?> reference);

    Set<Ref<?>> getReferenceQueue();

    void addReferenceCollectionToQueue(RefCollection<?> referenceCollection);

    Set<RefCollection<?>> getReferenceCollectionQueue();

    TorchFactory getTorchFactory();

}