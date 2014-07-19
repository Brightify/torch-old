package org.brightify.torch;

import org.brightify.torch.action.ActionSelector;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Torch extends ActionSelector {

    /**
     * @return A factory that created this instance of Torch
     */
    TorchFactory getFactory();

}
