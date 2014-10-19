package org.brightify.torch.action.load;

import org.brightify.torch.util.async.Callback;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ListLoader<ENTITY> extends Iterable<ENTITY> {

    List<ENTITY> list();

    ENTITY single();

    void list(Callback<List<ENTITY>> callback);

    void single(Callback<ENTITY> callback);

}
