package org.brightify.torch.action.load.async;

import org.brightify.torch.util.Callback;

import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface AsyncTypedLoader<ENTITY> {

    void id(Callback<ENTITY> callback, final long id);

    void ids(Callback<List<ENTITY>> callback, final Long... ids);

    void ids(Callback<List<ENTITY>> callback, final Iterable<Long> ids);

}
