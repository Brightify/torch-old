package org.brightify.torch;

import java.util.HashSet;
import java.util.Set;

public class SaveContainerImpl implements SaveContainer {

    private final TorchFactory torchFactory;
    private final Set<Class<?>> saveGroups;
    private final Set<Ref<?>> referenceQueue = new HashSet<Ref<?>>();
    private final Set<RefCollection<?>> referenceCollectionQueue = new HashSet<RefCollection<?>>();

    public SaveContainerImpl(TorchFactory torchFactory, Set<Class<?>> saveGroups) {
        this.torchFactory = torchFactory;
        this.saveGroups = saveGroups;
    }

    @Override
    public Set<Class<?>> getSaveGroups() {
        return saveGroups;
    }

    @Override
    public void addReferenceToQueue(Ref<?> reference) {
        referenceQueue.add(reference);
    }

    @Override
    public Set<Ref<?>> getReferenceQueue() {
        return referenceQueue;
    }

    @Override
    public void addReferenceCollectionToQueue(RefCollection<?> referenceCollection) {
        referenceCollectionQueue.add(referenceCollection);
    }

    @Override
    public Set<RefCollection<?>> getReferenceCollectionQueue() {
        return referenceCollectionQueue;
    }

    @Override
    public TorchFactory getTorchFactory() {
        return torchFactory;
    }




    /*
    @Override
    public Set<Class<?>> getLoadGroups() {
        return loadGroups;
    }

    @Override
    public <CHILD> Ref<CHILD> requestReference(Long childId, Class<CHILD> childClass) {
        EntityDescription<CHILD> childDescription = torchFactory.getEntities().getDescription(childClass);

        return new RefImpl<CHILD>(childDescription, torchFactory, childId);
    }

    @Override
    public void addReferenceToQueue(Ref<?> reference) {
        referenceQueue.add(reference);
    }

    @Override
    public Set<Ref<?>> getReferenceQueue() {
        return referenceQueue;
    }

    @Override
    public <CHILD> RefCollection<CHILD> requestReferenceCollection(Long parentId, Class<CHILD> childClass) {
        return null;
    }

    @Override
    public void addReferenceCollectionToQueue(RefCollection<?> referenceCollection) {
        referenceCollectionQueue.add(referenceCollection);
    }

    @Override
    public Set<RefCollection<?>> getReferenceCollectionQueue() {
        return referenceCollectionQueue;
    }

    @Override
    public TorchFactory getTorchFactory() {
        return torchFactory;
    }*/
}
