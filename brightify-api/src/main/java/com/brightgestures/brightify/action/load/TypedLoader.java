package com.brightgestures.brightify.action.load;

import com.brightgestures.brightify.Result;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface TypedLoader<ENTITY> {

    Result<ENTITY> id(long id);
    Result<List<ENTITY>> ids(Long... ids);
    Result<List<ENTITY>> ids(Collection<Long> ids);

}
