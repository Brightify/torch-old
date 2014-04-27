package org.brightify.torch.util;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface LazyArrayList<ENTITY> extends List<ENTITY> {

    ENTITY loadIfNeeded(int i);

    void loadAll();
}
