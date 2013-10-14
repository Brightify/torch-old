package com.brightgestures.brightify.action.load.api;

import com.brightgestures.brightify.Result;

import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface LoadResult<ENTITY> extends Result<List<ENTITY>>, Iterable<ENTITY> {

}
