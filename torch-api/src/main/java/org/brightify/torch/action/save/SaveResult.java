package org.brightify.torch.action.save;

import org.brightify.torch.Key;
import org.brightify.torch.Result;

import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface SaveResult<ENTITY> extends Result<Map<Key<ENTITY>, ENTITY>> {
}
