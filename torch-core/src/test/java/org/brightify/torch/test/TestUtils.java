package org.brightify.torch.test;

import java.lang.reflect.Array;
import java.util.UUID;

public class TestUtils {

    public static TestObject createTestObject() {
        TestObject testObject = new TestObject();

        testObject.stringField = UUID.randomUUID().toString();
        testObject.longField = 98765432123456789L;
        testObject.intField = 123456789;

        return testObject;
    }

    public static <T> T[] concatArrays(T[] first, T[] second) {
        int firstLength = first.length;
        int secondLength = second.length;

        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(first.getClass().getComponentType(), firstLength + secondLength);
        System.arraycopy(first, 0, result, 0, firstLength);
        System.arraycopy(second, 0, result, firstLength, secondLength);

        return result;
    }

}
