package com.brightgestures.brightify;

import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.test.TestObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void keyStringConversion() {
        Key<TestObject> key = Key.create(TestObject.class, 1L);

        assertNotNull(key);

        byte[] keyData = Key.keyToByteArray(key);

//        assertEquals(TestObject.class.getName() + Key.KEY_KIND_DELIMITER + 1L, keyData);

        Key<TestObject> newKey = Key.keyFromByteArray(keyData);

        assertNotNull(newKey);
        assertEquals(key.getId(), newKey.getId());
        assertEquals(key.getKind(), newKey.getKind());
        assertEquals(key.getType(), newKey.getType());
    }

    @Test
    public void keyListStringConversion() {
        Key<TestObject> key1 = Key.create(TestObject.class, 1L);
        Key<TestObject> key2 = Key.create(TestObject.class, 1000L);

        List<Key<TestObject>> keys = new ArrayList<Key<TestObject>>();
        keys.add(key1);
        keys.add(key2);

        byte[] keyData = Key.keysToByteArray(keys);

        //assertEquals(TestObject.class.getName() + Key.KEY_KIND_DELIMITER + 1L + Key.KEY_ID_DELIMITER + 1000L, keyData);

        List<Key<TestObject>> newKeys = Key.keysFromByteArray(keyData);

        assertNotNull(newKeys);
        assertEquals(keys.size(), newKeys.size());

        Key<TestObject> newKey1 = keys.get(0);
        Key<TestObject> newKey2 = keys.get(1);

        assertNotNull(newKey1);
        assertNotNull(newKey2);

        assertEquals(key1.getId(), newKey1.getId());
        assertEquals(key1.getKind(), newKey1.getKind());
        assertEquals(key1.getType(), newKey1.getType());

        assertEquals(key2.getId(), newKey2.getId());
        assertEquals(key2.getKind(), newKey2.getKind());
        assertEquals(key2.getType(), newKey2.getType());
    }
}
