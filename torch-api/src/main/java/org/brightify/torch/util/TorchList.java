package org.brightify.torch.util;

import org.brightify.torch.Torch;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface TorchList<BACKING_ENTITY extends KeyValueEntity<Long, E>, E> extends List<E> {

    void save(Torch torch);

}
