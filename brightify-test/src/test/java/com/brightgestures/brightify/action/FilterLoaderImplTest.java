package com.brightgestures.brightify.action;

import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.BrightifyService;
import com.brightgestures.brightify.action.load.InitialLoader;
import com.brightgestures.brightify.action.load.impl.InitialLoaderImpl;
import com.brightgestures.brightify.test.TestObject;
import org.junit.Test;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class FilterLoaderImplTest {

    @Test
    public void andTest() {
        InitialLoader l = new InitialLoaderImpl(null);



        l.type(TestObject.class).filter("f1", "v1").and().nest().filter("f2", "v2").or().filter("f3", "v3").close();

/*        Filterable f = new FilterLoaderImpl();

        f.filter("f1", "v1").and().nest().filter("f2", "v2").and().filter("f3", "v3").close();
*/
    }

}
