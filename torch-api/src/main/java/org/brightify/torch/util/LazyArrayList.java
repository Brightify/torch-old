package org.brightify.torch.util;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface LazyArrayList<ENTITY> {
    ENTITY get(int i);

    ENTITY set(int i, ENTITY t);

    boolean contains(Object o);

    ENTITY loadIfNeeded(int i);

    void loadAll();
}
