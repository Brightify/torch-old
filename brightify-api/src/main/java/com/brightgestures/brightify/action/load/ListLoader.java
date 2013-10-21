package com.brightgestures.brightify.action.load;

import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface ListLoader<ENTITY> extends Iterable<ENTITY> {

    List<ENTITY> list();

}
