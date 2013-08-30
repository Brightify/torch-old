package com.brightgestures.brightify;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class KeyTest {

    @Test
    public void testCreate() throws Exception {
        Key<TestObject> key = Key.create(TestObject.class, 1L);

        assertNotNull(key);
        assertEquals(1L, key.getId());
        assertEquals("TestObject", key.getKind());
    }
}
