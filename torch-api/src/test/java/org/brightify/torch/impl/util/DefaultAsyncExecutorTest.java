package org.brightify.torch.impl.util;

import org.brightify.torch.impl.util.async.DefaultAsyncExecutor;
import org.brightify.torch.util.async.Callback;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DefaultAsyncExecutorTest {

    @Test
    public void callbackSuccessInvoked() throws Exception {
        DefaultAsyncExecutor executor = new DefaultAsyncExecutor();

        Callable<Boolean> task = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Thread.sleep(500);
                return true;
            }
        };

        final CountDownLatch latch = new CountDownLatch(1);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                assertThat(data, is(true));
                latch.countDown();
            }

            @Override
            public void onFailure(Exception e) {

            }
        };

        executor.submit(callback, task);

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void callbackFailureInvoked() throws Exception {
        final Exception exception = new Exception("Test exception!");

        DefaultAsyncExecutor executor = new DefaultAsyncExecutor();

        Callable<Boolean> task = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Thread.sleep(500);
                throw exception;
            }
        };

        final CountDownLatch latch = new CountDownLatch(1);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
            }

            @Override
            public void onFailure(Exception e) {
                assertThat(e, is(exception));
                latch.countDown();
            }
        };

        executor.submit(callback, task);

        latch.await(5, TimeUnit.SECONDS);

    }

}