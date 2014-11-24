package org.brightify.torch.action;

import org.brightify.torch.action.load.Loader;
import org.brightify.torch.action.delete.Deleter;
import org.brightify.torch.action.save.Saver;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ActionSelector {
    /**
     * Initialize delete operation.
     *
     * @return New instance of deleter.
     */
    Deleter delete();

    /**
     * Initialize load operation.
     *
     * @return New instance of loader.
     */
    Loader load();

    /**
     * Initialize save operation.
     *
     * @return New instance of saver.
     */
    Saver save();
}
