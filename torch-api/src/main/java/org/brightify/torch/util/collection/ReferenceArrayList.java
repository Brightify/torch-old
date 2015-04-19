package org.brightify.torch.util.collection;

import org.brightify.torch.RefCollection;
import org.brightify.torch.TorchFactory;

import java.util.ArrayList;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ReferenceArrayList<E> extends ArrayList<E> implements RefCollection<E> {

    private final Long parentId;

    public ReferenceArrayList(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    @Override
    public void set(Long position, TorchFactory torchFactory, Long childId) {

    }

    @Override
    public void set(Long position, E e) {

    }
}
