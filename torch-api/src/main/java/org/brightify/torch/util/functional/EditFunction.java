package org.brightify.torch.util.functional;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface EditFunction<INPUT> {
    /**
     *
     * @param input
     * @return true to save the input, false otherwise
     */
    boolean apply(INPUT input);
}
