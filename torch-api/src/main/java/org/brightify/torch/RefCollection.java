package org.brightify.torch;

import java.util.Collection;

public interface RefCollection<CHILD> extends Collection<CHILD> {

    Long getParentId();

    void set(Long position, TorchFactory torchFactory, Long childId);

    void set(Long position, CHILD child);

}
