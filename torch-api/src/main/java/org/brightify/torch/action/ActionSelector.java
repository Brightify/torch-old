package org.brightify.torch.action;

import org.brightify.torch.action.delete.Deleter;
import org.brightify.torch.action.load.sync.Loader;
import org.brightify.torch.action.relation.RelationResolver;
import org.brightify.torch.action.save.Saver;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ActionSelector {
    /**
     * Initialize delete operation.
     */
    Deleter delete();

    /**
     * Initialize load operation.
     */
    Loader load();

    /**
     * Initialize save operation.
     */
    Saver save();

    /**
     * Initialize relation mapping operation. You should not need to perform this operation yourself.
     */
    RelationResolver relation();
}
