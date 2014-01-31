package org.brightify.torch.action.delete;

import org.brightify.torch.Key;
import org.brightify.torch.Result;

import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface DeleteResult<ENTITY> extends Result<Map<Key<ENTITY>, Boolean>> {
}
