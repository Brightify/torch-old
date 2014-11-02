package org.brightify.torch.util.functional;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface FoldingFunction<ACCUMULATOR, INPUT> {
    ACCUMULATOR apply(ACCUMULATOR accumulator, INPUT input);
}
