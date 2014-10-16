package org.brightify.torch.action.load.sync;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ListLoader<ENTITY> extends Iterable<ENTITY> {

    List<ENTITY> list();

    ENTITY single();

}
