package org.brightify.torch.util.async;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
* @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
*/
public interface AsyncExecutor {
    <RESULT> Future<AsyncResult<RESULT>> submit(final Callback<RESULT> callback, final Callable<RESULT> task);
}
