package com.brightgestures.droidorm.util;

import com.brightgestures.droidorm.Result;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public abstract class ResultWrapper<F, T> implements Result<T> {

    private Result<F> mFrom;

    public ResultWrapper(Result<F> from) {
        mFrom = from;
    }

    protected abstract T wrap(F original);

    @Override
    public T now() {
        return wrap(mFrom.now());
    }
}
