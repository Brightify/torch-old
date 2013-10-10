package com.brightgestures.brightify.action.load;

import com.brightgestures.brightify.Result;
import com.brightgestures.brightify.action.load.filter.Closeable;
import com.brightgestures.brightify.action.load.filter.Filterable;
import com.brightgestures.brightify.action.load.filter.Nestable;
import com.brightgestures.brightify.action.load.filter.OperatorFilter;

import java.util.List;

/**
* @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
*/
public interface TypedLoader<E> extends Loader, GenericLoader<E>, Filterable<E>, Nestable<E>, ListLoader<E> {
    Result<E> id(Long id);

    <T extends ListLoader<E> & Closeable<E> & OperatorFilter<E>> Result<List<E>> ids(Long... ids);

    TypedLoader<E> group(Class<?> group);

    TypedLoader<E> groups(Class<?>... groups);
}
