package com.brightgestures.brightify.action.load;

import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Result;

/**
* @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
*/
public interface InitialLoader {
    <E> Result<E> key(Key<E> key);

    <E> TypedLoader<E> type(Class<E> type);

    <E> Result<E> entity(E entity);

    InitialLoader group(Class<?> group);

    InitialLoader groups(Class<?>... groups);
}
