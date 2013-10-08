package com.brightgestures.brightify.action;

import com.brightgestures.brightify.action.load.FilterLoaderImpl;
import com.brightgestures.brightify.action.load.filter.Filterable;
import org.junit.Test;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class FilterLoaderImplTest {

    @Test
    public void andTest() {

        Filterable f = new FilterLoaderImpl();

        f.filter("f1", "v1").and().nest().filter("f2", "v2").and().filter("f3", "v3").close();

    }

}
