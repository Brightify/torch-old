package org.brightify.torch.action.load;

import org.brightify.torch.util.async.Callback;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface TypedLoader<ENTITY> {

    ENTITY id(long id);

    List<ENTITY> ids(Long... ids);

    List<ENTITY> ids(Iterable<Long> ids);

    void id(Callback<ENTITY> callback, long id);

    void ids(Callback<List<ENTITY>> callback, Long... ids);

    void ids(Callback<List<ENTITY>> callback, Iterable<Long> ids);

}
