package org.brightify.torch.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class Serializer {

    private Serializer() {
        throw new UnsupportedOperationException("No instances!");
    }


    public static byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
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
        return serializeList(Arrays.asList(array));
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] deserializeArray(T[] arrayType, Class<T> objectType, byte[] blob)
            throws IOException, ClassNotFoundException {
        List<T> list = deserializeArrayList(objectType, blob);

        return list.toArray(arrayType);
    }

    public static <T> byte[] serializeList(List<T> list) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);

        int count = list.size();
        stream.writeInt(count);

        for (T object : list) {
            byte[] data = serialize(object);

            stream.writeInt(data.length);

            stream.write(data, 0, data.length);
        }

        stream.flush();

        return byteArrayOutputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public static <T> ArrayList<T> deserializeArrayList(Class<T> propertyClass, byte[] blob)
            throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(blob);
        DataInputStream stream = new DataInputStream(byteArrayInputStream);

        int count = stream.readInt();

        ArrayList<T> collection = new ArrayList<T>();

        for (int i = 0; i < count; i++) {
            int length = stream.readInt();
            int read = 0;
            byte[] data = new byte[length];

            do {
                read += stream.read(data, read, length - read);
            } while (read < length);

            T object = (T) deserialize(data);

            collection.add(object);
        }

        return collection;
    }

}

