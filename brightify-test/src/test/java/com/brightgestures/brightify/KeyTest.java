package com.brightgestures.brightify;

import com.brightgestures.brightify.marshall.stream.ArrayListStreamMarshaller;
import com.brightgestures.brightify.marshall.stream.KeyStreamMarshaller;
import com.brightgestures.brightify.test.TestObject;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
        Key<TestObject> key = KeyFactory.create(TestObject.class, 1L);

        assertNotNull(key);
        assertEquals(1L, key.getId());
        assertEquals("TestObject", key.getType().getSimpleName());
    }

    @Test
    public void keyStringConversion() throws Exception {
        Key<TestObject> key = KeyFactory.create(TestObject.class, 1L);

        assertNotNull(key);

        KeyStreamMarshaller<TestObject> marshaller = KeyStreamMarshaller.getInstance(TestObject.class);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);

        marshaller.marshall(outputStream, key);

        outputStream.flush();

        byte[] keyData = byteArrayOutputStream.toByteArray();

//        assertEquals(TestObject.class.getName() + Key.KEY_KIND_DELIMITER + 1L, keyData);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(keyData);
        DataInputStream inputStream = new DataInputStream(byteArrayInputStream);

        Key<TestObject> newKey = marshaller.unmarshall(inputStream);

        assertNotNull(newKey);
        assertEquals(key.getId(), newKey.getId());
        assertEquals(key.getType(), newKey.getType());
    }

    @Test
    public void keyListStringConversion() throws Exception {
        Key<TestObject> key1 = KeyFactory.create(TestObject.class, 1L);
        Key<TestObject> key2 = KeyFactory.create(TestObject.class, 1000L);

        List<Key<TestObject>> keys = new ArrayList<Key<TestObject>>();
        keys.add(key1);
        keys.add(key2);

        ArrayListStreamMarshaller<Key<TestObject>> marshaller = ArrayListStreamMarshaller.getInstance(
                KeyStreamMarshaller.getInstance(TestObject.class));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);

        marshaller.marshall(outputStream, keys);

        outputStream.flush();

        byte[] keyData =  byteArrayOutputStream.toByteArray();

        //assertEquals(TestObject.class.getName() + Key.KEY_KIND_DELIMITER + 1L + Key.KEY_ID_DELIMITER + 1000L, keyData);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(keyData);
        DataInputStream inputStream = new DataInputStream(byteArrayInputStream);

        List<Key<TestObject>> newKeys = marshaller.unmarshall(inputStream);

        assertNotNull(newKeys);
        assertEquals(keys.size(), newKeys.size());

        Key<TestObject> newKey1 = keys.get(0);
        Key<TestObject> newKey2 = keys.get(1);

        assertNotNull(newKey1);
        assertNotNull(newKey2);

        assertEquals(key1.getId(), newKey1.getId());
        assertEquals(key1.getType(), newKey1.getType());

        assertEquals(key2.getId(), newKey2.getId());
        assertEquals(key2.getType(), newKey2.getType());
    }
}
