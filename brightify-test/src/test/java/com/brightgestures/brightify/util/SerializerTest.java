package com.brightgestures.brightify.util;

import com.brightgestures.brightify.test.TestObject;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class SerializerTest {

    @Test
    public void testIntegerSerialization() throws Exception {
        int int1 = Integer.MAX_VALUE;
        byte[] data = Serializer.serialize(int1);
        int int2 = Serializer.deserialize(data, int.class);
        assertEquals(int1, int2);
    }

    @Test
    public void testLongSerialization() throws Exception {
        long long1 = Long.MAX_VALUE;
        byte[] data = Serializer.serialize(long1);
        long long2 = Serializer.deserialize(data, long.class);
        assertEquals(long1, long2);
    }

    @Test
    public void testStringSerialization() throws Exception {
        String string1 = "test_testdj24jd3dm3k";
        byte[] data = Serializer.serialize(string1);
        String string2 = Serializer.deserialize(data, String.class);
        assertEquals(string1, string2);
    }

    @Test
    public void testObjectSerialization() throws Exception {
        TestObject expectedObject = new TestObject();
        byte[] data = Serializer.serialize(expectedObject);

        TestObject deserializedObject = Serializer.deserialize(data, TestObject.class);

        assertEquals(expectedObject, deserializedObject);
    }

    @Test
    public void testArraySerialization() throws Exception {
        TestObject[] array1 = new TestObject[10];
        for(int i = 0; i < array1.length; i++) {
            array1[i] = new TestObject();
            array1[i].id = (long) i;
        }

        byte[] data = Serializer.serializeArray(array1);

        TestObject[] array2 = Serializer.deserializeArray(new TestObject[0], TestObject.class, data);

        assertArrayEquals(array1, array2);
    }

    @Test
    public void testCollectionSerialization() throws Exception {
        ArrayList<TestObject> list1 = new ArrayList<TestObject>();
        for(int i = 0; i < 10; i++) {
            TestObject testObject = new TestObject();
            testObject.id = (long) i;
            list1.add(testObject);
        }

        byte[] data = Serializer.serializeCollection(list1);

        ArrayList<TestObject> list2 = Serializer.deserializeCollection(ArrayList.class, TestObject.class, data);

        assertEquals(list1, list2);
    }

}
