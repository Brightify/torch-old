package com.brightgestures.brightify.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class Serializer {

    private Serializer() {
        throw new UnsupportedOperationException();
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        return byteArrayOutputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] bytes, Class<T> objectClass) throws IOException, ClassNotFoundException {
        return (T) deserialize(bytes);
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return objectInputStream.readObject();
    }

    public static <T> byte[] serializeArray(T[] array) throws IOException {
        return serializeCollection(Arrays.asList(array));
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] deserializeArray(T[] arrayType, Class<T> objectType, byte[] blob) throws IOException, ClassNotFoundException {
        List<T> list = deserializeCollection(List.class, objectType, blob);

        return list.toArray(arrayType);
    }

    public static <T> byte[] serializeCollection(Collection<T> collection) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);

        int count = collection.size();
        stream.writeInt(count);

        for(T object : collection) {
            byte[] data = serialize(object);

            stream.writeInt(data.length);

            stream.write(data, 0, data.length);
        }

        stream.flush();

        return byteArrayOutputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public static <T, C extends Collection<T>> C deserializeCollection(Class<C> collectionClass, Class<T> propertyClass, byte[] blob) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(blob);
        DataInputStream stream = new DataInputStream(byteArrayInputStream);

        int count = stream.readInt();

        C collection = TypeUtilsCompatibility.constructCollection(collectionClass, count);

        for(int i = 0; i < count; i++) {
            int length = stream.readInt();

            byte[] data = new byte[length];

            stream.read(data, 0, length);

            T object = (T) deserialize(data);

            collection.add(object);
        }

        return collection;
    }

}
