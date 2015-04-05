package org.brightify.torch;

import org.brightify.torch.filter.ReferenceProperty;

import java.util.HashSet;
import java.util.Set;

public class LoadContainerImpl implements LoadContainer {

    private final Set<Class<?>> loadGroups;
    private final TorchFactory torchFactory;
    private final Set<Ref<?>> referenceQueue = new HashSet<Ref<?>>();
    private final Set<RefCollection<?>> referenceCollectionQueue = new HashSet<RefCollection<?>>();

    public LoadContainerImpl(TorchFactory torchFactory, Set<Class<?>> loadGroups) {
        this.torchFactory = torchFactory;
        this.loadGroups = loadGroups;
    }

    public LoadContainerImpl(LoadContainer otherContainer) {
        this.torchFactory = otherContainer.getTorchFactory();
        this.loadGroups = otherContainer.getLoadGroups();
    }

    @Override
    public Set<Class<?>> getLoadGroups() {
        return loadGroups;
    }

    @Override
    public <OWNER, CHILD> Ref<CHILD> requestReference(ReferenceProperty<OWNER, CHILD> referenceProperty, Long childId) {
        EntityDescription<CHILD> childDescription = torchFactory.getEntities().getDescription(referenceProperty.getReferencedType());

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
    }
}
