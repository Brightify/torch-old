package com.brightgestures.brightify.action;

import android.test.mock.MockContext;
import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.BrightifyFactory;
import com.brightgestures.brightify.BrightifyService;
import com.brightgestures.brightify.action.load.ChildLoader;
import com.brightgestures.brightify.action.load.InitialLoader;
import com.brightgestures.brightify.action.load.LoadQuery;
import com.brightgestures.brightify.action.load.impl.InitialLoaderImpl;
import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.test.MainTestActivity;
import com.brightgestures.brightify.test.TestObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.net.NetPermission;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@RunWith(RobolectricTestRunner.class)
public class FilterLoaderImplTest {

    @Test
    public void andTest() {
        MainTestActivity activity = Robolectric.buildActivity(MainTestActivity.class).create().get();


        BrightifyService.load(activity);

        BrightifyService.factory().register(TestObject.class);



        List<TestObject> list = BrightifyService.bfy(activity)
                .load()
                .group(ChildLoader.class)
                .groups(NetPermission.class, FilterLoaderImplTest.class)
                    .type(TestObject.class)
                    .group(Entity.class)
                    .filter("intField>?", 1)
                    .or()
                    .nest()
                        .filter("1=?", 1)
                        .and()
                        .filter("2<?", 3)
                    .close()
                .list();

        int i = list.size();

/*        Filterable f = new FilterLoaderImpl();

        f.filter("f1", "v1").and().nest().filter("f2", "v2").and().filter("f3", "v3").close();
*/
    }

}
