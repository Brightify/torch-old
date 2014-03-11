package org.brightify.torch.action;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface AsyncSelector<ACTION> {

    /**
     * Begin async operation. At the end of the operation you'll be able to provide a callback and the operation will
     * continue in background.
     */
    ACTION async();

}
