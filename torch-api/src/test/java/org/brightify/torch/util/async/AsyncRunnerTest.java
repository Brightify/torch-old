package org.brightify.torch.util.async;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class AsyncRunnerTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void runIsSynchronous() throws Exception {
        final long timeout = 5000;

        Callable<Boolean> task = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Thread.sleep(timeout);
                return false;
            }
        };

        long startTime = System.currentTimeMillis();

        Boolean result = AsyncRunner.run(task);

        long endTime = System.currentTimeMillis();

        assertThat(result, is(false));
        assertThat(endTime - startTime, greaterThanOrEqualTo(timeout));
    }

    @Test
    public void submitIsAsynchronous() throws Exception {
        final long timeout = 5000;
        final long testedCount = timeout / 5;

        Callable<Boolean> task = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Thread.sleep(timeout);
                return false;
            }
        };

        final CountDownLatch latch = new CountDownLatch(1);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                latch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
            }
        };

        Future<AsyncResult<Boolean>> resultFuture = AsyncRunner.submit(callback, task);

        long count = 0;
        while (latch.getCount() > 0) {
            count++;
            Thread.sleep(1);
        }

        AsyncResult<Boolean> result = resultFuture.get();
        assertThat(result.getData(), is(false));
        assertThat(count, greaterThanOrEqualTo(testedCount));
    }

    @Test
    public void exceptionPassedToResult() throws Exception {
        final Exception exception = new Exception("Test exception!");

        Callable<Boolean> task = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                throw exception;
            }
        };

        Future<AsyncResult<Boolean>> resultFuture = AsyncRunner.submit(null, task);

        AsyncResult<Boolean> result = resultFuture.get();

        assertThat(result.getData(), is(nullValue()));
        assertThat(result.getException(), is(exception));
    }

    @Test
    public void exceptionIsThrownOnUnwrap() throws Exception {
        final UnsupportedOperationException testedException = new UnsupportedOperationException("Test exception!");

        Callable<Boolean> task = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                throw testedException;
            }
        };

        Future<AsyncResult<Boolean>> resultFuture = AsyncRunner.submit(null, task);

        AsyncResult<Boolean> result = resultFuture.get();

        exception.expect(AsyncExecutionException.class);

        result.unwrap();
    }

}
