package org.brightify.torch.util;

import org.brightify.torch.Result;

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

    @Override
    public void async(Callback<T> callback) {
        AsyncRunner.run(new AsyncRunner.Task<T>() {
            @Override
            public T doWork() throws Exception {
                return now();
            }
        }, callback);
    }
}
