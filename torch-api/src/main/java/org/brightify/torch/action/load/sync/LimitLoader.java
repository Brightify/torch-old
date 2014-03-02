package org.brightify.torch.action.load.sync;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface LimitLoader<ENTITY> {

    OffsetListLoader<ENTITY> limit(int limit);

}
