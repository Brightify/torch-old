package org.brightify.torch;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;
import org.brightify.torch.android.AndroidSQLiteEngine;
import org.brightify.torch.test.MainTestActivity;
import org.brightify.torch.test.TestObject;
import org.brightify.torch.util.async.Callback;
import org.brightify.torch.util.Constants;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class TorchServiceTest extends ActivityInstrumentationTestCase2<MainTestActivity> {

    public TorchServiceTest() {
        super(MainTestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @UiThreadTest
    @MediumTest
    public void testInitialization() {
        final AndroidSQLiteEngine engine =
                new AndroidSQLiteEngine(getActivity().getApplicationContext(), Constants.DEFAULT_DATABASE_NAME, null);

        TorchService.with(engine);

        engine.wipe();

        TorchService.forceUnload();
    }

    @MediumTest
    public void testAsyncInitialization() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        final AndroidSQLiteEngine engine =
                new AndroidSQLiteEngine(getActivity().getApplicationContext(), Constants.DEFAULT_DATABASE_NAME, null);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TorchService.asyncInit(new Callback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        throw new RuntimeException(e);
                    }
                }).with(engine)
                        .register(TestObject.class)
                        .submit();
            }
        });


        assertTrue(latch.await(30, TimeUnit.SECONDS));

        assertNotNull(engine.getDatabase());

        engine.wipe();

        TorchService.forceUnload();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
