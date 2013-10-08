package com.brightgestures.brightify.action;

import com.brightgestures.brightify.action.load.InitialLoader;
import com.brightgestures.brightify.action.load.impl.InitialLoaderImpl;
import com.brightgestures.brightify.action.load.impl.LoaderImpl;
import com.brightgestures.brightify.action.load.filter.Filterable;
import com.brightgestures.brightify.test.TestObject;
import org.junit.Test;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class LoaderImplTest {

    @Test
    public void andTest() {
        InitialLoader l = new InitialLoaderImpl(null);

        l.type(TestObject.class);

/*        Filterable f = new LoaderImpl();

        f.filter("f1", "v1").and().nest().filter("f2", "v2").and().filter("f3", "v3").close();
*/
    }

}
