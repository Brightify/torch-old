package org.brightify.torch.action.load.async;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface AsyncOffsetLoader<ENTITY> {

    AsyncListLoader<ENTITY> offset(int offset);

}
