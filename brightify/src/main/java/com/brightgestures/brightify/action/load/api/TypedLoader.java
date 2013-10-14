package com.brightgestures.brightify.action.load.api;

import com.brightgestures.brightify.Result;
import com.brightgestures.brightify.action.load.filter.Closeable;
import com.brightgestures.brightify.action.load.filter.Filterable;
import com.brightgestures.brightify.action.load.filter.Nestable;
import com.brightgestures.brightify.action.load.filter.OperatorFilter;

import java.util.List;

/**
* @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
*/
public interface TypedLoader<ENTITY> extends Loader, GenericLoader<ENTITY>, Filterable<ENTITY>, Nestable<ENTITY>,
        ListLoader<ENTITY>, OrderLoader<ENTITY>, LimitLoader<ENTITY> {
    Result<ENTITY> id(Long id);

    <T extends ListLoader<ENTITY> & Closeable<ENTITY> & OperatorFilter<ENTITY>> Result<List<ENTITY>> ids(Long... ids);

    TypedLoader<ENTITY> group(Class<?> group);

    TypedLoader<ENTITY> groups(Class<?>... groups);
}
