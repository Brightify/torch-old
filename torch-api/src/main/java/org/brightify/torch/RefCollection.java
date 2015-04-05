package org.brightify.torch;

import org.brightify.torch.filter.ReferenceProperty;

import java.util.Collection;

public interface RefCollection<CHILD> extends Collection<CHILD> {

    Class<?>[] getLoadGroups();

    Long getParentId();

    ReferenceProperty<?, CHILD> getParentProperty();

    EntityDescription<?> getParentDescription();

    EntityDescription<CHILD> getChildDescription();

    void set(Long position, TorchFactory torchFactory, Long childId);

    void set(Long position, CHILD child);

}
