package com.brightgestures.brightify;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.AndroidTestCase;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;
import com.brightgestures.brightify.BrightifyService;
import com.brightgestures.brightify.test.MainTestActivity;
import com.brightgestures.brightify.test.TestObject;
import com.brightgestures.brightify.util.Callback;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class BrightifyServiceTest extends ActivityInstrumentationTestCase2<MainTestActivity> {

    public BrightifyServiceTest() {
        super(MainTestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @UiThreadTest
    @MediumTest
    public void testInitialization() {
        BrightifyService.with(getActivity());

        assertNotNull(BrightifyService.factory().forceOpenOrCreateDatabase());

        BrightifyService.factory().deleteDatabase();

        BrightifyService.forceUnload();
    }

    @MediumTest
    public void testAsyncInitialization() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BrightifyService.asyncInit(new Callback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        throw new RuntimeException(e);
                    }
                }).with(getActivity())
                        .register(TestObject.class)
                        .submit();
            }
        });


        assertTrue(latch.await(30, TimeUnit.SECONDS));

        assertNotNull(BrightifyService.bfy().getDatabase());

        BrightifyService.factory().deleteDatabase();

        BrightifyService.forceUnload();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
