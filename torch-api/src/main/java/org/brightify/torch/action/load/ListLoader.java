package org.brightify.torch.action.load;

import org.brightify.torch.util.async.Callback;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ListLoader<ENTITY> extends Iterable<ENTITY> {

    ProcessingLoader<ENTITY> process();

    // RawPropertyLoader<ENTITY> raw();

    /**
     * Loads and returns a list of all entities matched by this loader.
     */
    List<ENTITY> list();

    /**
     * Loads and returns first entity from a list of all entities matched by this filter or null if the list is empty.
     */
    ENTITY single();

    void list(Callback<List<ENTITY>> callback);

    void single(Callback<ENTITY> callback);

}
