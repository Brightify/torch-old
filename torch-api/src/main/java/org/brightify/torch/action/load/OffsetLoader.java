package org.brightify.torch.action.load;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface OffsetLoader<ENTITY> {

    ListLoader<ENTITY> offset(int offset);

}
