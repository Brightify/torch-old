package org.brightify.torch.util.async;

import org.junit.Test;

import java.util.concurrent.Callable;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class AsyncRunnerTest {

    @Test
    public void defaultExecutorGetsSet() throws Exception {
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

}
