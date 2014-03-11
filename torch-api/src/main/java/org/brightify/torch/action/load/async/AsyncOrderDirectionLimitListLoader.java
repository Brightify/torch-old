package org.brightify.torch.action.load.async;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface AsyncOrderDirectionLimitListLoader<ENTITY>
        extends AsyncOrderLoader<ENTITY>, AsyncDirectionLoader<ENTITY>,
        AsyncLimitLoader<ENTITY>, AsyncListLoader<ENTITY>, AsyncCountable {
}
