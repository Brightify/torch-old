package org.brightify.torch;

import org.brightify.torch.filter.ReferenceProperty;

import java.util.Set;

public interface LoadContainer {

    Set<Class<?>> getLoadGroups();

    <OWNER, CHILD> Ref<CHILD> requestReference(ReferenceProperty<OWNER, CHILD> referenceProperty, Long childId);

    void addReferenceToQueue(Ref<?> reference);

    Set<Ref<?>> getReferenceQueue();

    <CHILD> RefCollection<CHILD> requestReferenceCollection(Long parentId, Class<CHILD> childClass);

    void addReferenceCollectionToQueue(RefCollection<?> referenceCollection);

    Set<RefCollection<?>> getReferenceCollectionQueue();

    TorchFactory getTorchFactory();

}