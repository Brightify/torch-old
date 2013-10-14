package com.brightgestures.brightify.action.load.api;

import java.util.List;

/**
* @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
*/
public interface ListLoader<E> extends Iterable<E> {
    List<E> list();
}
