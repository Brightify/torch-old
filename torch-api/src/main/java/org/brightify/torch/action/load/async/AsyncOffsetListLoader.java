package org.brightify.torch.action.load.async;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface AsyncOffsetListLoader<ENTITY>
        extends AsyncOffsetLoader<ENTITY>, AsyncListLoader<ENTITY>, AsyncCountable {
}
