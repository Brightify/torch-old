package org.brightify.torch.action.load.async;

import org.brightify.torch.util.async.Callback;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface AsyncListLoader<ENTITY> {

    void list(Callback<List<ENTITY>> callback);

    void single(Callback<ENTITY> callback);

}
