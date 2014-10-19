package org.brightify.torch.action.load.combined;

import org.brightify.torch.action.load.Countable;
import org.brightify.torch.action.load.ListLoader;
import org.brightify.torch.action.load.OffsetLoader;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface OffsetListLoader<ENTITY> extends OffsetLoader<ENTITY>, ListLoader<ENTITY>, Countable {
}
