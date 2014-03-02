package org.brightify.torch.action.load.async;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface AsyncOrderLimitListLoader<ENTITY>
        extends AsyncOrderLoader<ENTITY>, AsyncLimitLoader<ENTITY>, AsyncListLoader<ENTITY>, AsyncCountable {
}
