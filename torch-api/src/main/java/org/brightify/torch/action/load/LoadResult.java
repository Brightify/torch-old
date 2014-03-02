package org.brightify.torch.action.load;

import org.brightify.torch.Result;
import org.brightify.torch.action.load.sync.Countable;

import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface LoadResult<ENTITY> extends Result<List<ENTITY>>, Iterable<ENTITY>, Countable {

}
