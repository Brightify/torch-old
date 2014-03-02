package org.brightify.torch.action;

import org.brightify.torch.action.delete.Deleter;
import org.brightify.torch.action.load.sync.Loader;
import org.brightify.torch.action.save.Saver;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface ActionSelector {
    /**
     * Initialize deletion.
     *
     * @return
     */
    Deleter delete();

    Loader load();

    Saver save();
}
