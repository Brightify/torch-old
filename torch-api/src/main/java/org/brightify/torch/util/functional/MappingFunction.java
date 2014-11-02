package org.brightify.torch.util.functional;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface MappingFunction<INPUT, OUTPUT> {
    OUTPUT apply(INPUT input);
}
