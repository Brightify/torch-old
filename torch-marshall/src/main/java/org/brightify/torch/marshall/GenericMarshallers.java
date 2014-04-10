package org.brightify.torch.marshall;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class GenericMarshallers {

    public static final Marshaller<Boolean> BOOLEAN_MARSHALLER = new Marshaller<Boolean>() {
        @Override
        public Boolean unmarshall(DataInputStream inputStream) throws IOException {
            return inputStream.readBoolean();
        }

        @Override
        public void marshall(DataOutputStream outputStream, Boolean value) throws IOException {
            outputStream.writeBoolean(value);
        }
    };
    public static final Marshaller<Byte> BYTE_MARSHALLER = new Marshaller<Byte>() {
        @Override
        public Byte unmarshall(DataInputStream inputStream) throws IOException {
            return inputStream.readByte();
        }

        @Override
        public void marshall(DataOutputStream outputStream, Byte value) throws IOException {
            outputStream.writeByte(value);
        }
    };
    public static final Marshaller<Short> SHORT_MARSHALLER = new Marshaller<Short>() {
        @Override
        public Short unmarshall(DataInputStream inputStream) throws IOException {
            return inputStream.readShort();
        }

        @Override
        public void marshall(DataOutputStream outputStream, Short value) throws IOException {
            outputStream.writeShort(value);
        }
    };
    public static final Marshaller<Integer> INTEGER_MARSHALLER = new Marshaller<Integer>() {
        @Override
        public Integer unmarshall(DataInputStream inputStream) throws IOException {
            return inputStream.readInt();
        }

        @Override
        public void marshall(DataOutputStream outputStream, Integer value) throws IOException {
            outputStream.writeInt(value);
        }
    };
    public static final Marshaller<Long> LONG_MARSHALLER = new Marshaller<Long>() {
        @Override
        public Long unmarshall(DataInputStream inputStream) throws IOException {
            return inputStream.readLong();
        }

        @Override
        public void marshall(DataOutputStream outputStream, Long value) throws IOException {
            outputStream.writeLong(value);
        }
    };
    public static final Marshaller<Float> FLOAT_MARSHALLER = new Marshaller<Float>() {
        @Override
        public Float unmarshall(DataInputStream inputStream) throws IOException {
            return inputStream.readFloat();
        }

        @Override
        public void marshall(DataOutputStream outputStream, Float value) throws IOException {
            outputStream.writeFloat(value);
        }
    };
    public static final Marshaller<Double> DOUBLE_MARSHALLER = new Marshaller<Double>() {
        @Override
        public Double unmarshall(DataInputStream inputStream) throws IOException {
            return inputStream.readDouble();
        }

        @Override
        public void marshall(DataOutputStream outputStream, Double value) throws IOException {
            outputStream.writeDouble(value);
        }
    };
    public static final Marshaller<String> STRING_MARSHALLER = new Marshaller<String>() {
        @Override
        public String unmarshall(DataInputStream inputStream) throws IOException {
            return inputStream.readUTF();
        }

        @Override
        public void marshall(DataOutputStream outputStream, String value) throws IOException {
            outputStream.writeUTF(value);
        }
    };

    private static final Map<Class<?>, Marshaller<?>> GENERIC_MARSHALLERS = new HashMap<Class<?>, Marshaller<?>>();

    static {
        GENERIC_MARSHALLERS.put(Boolean.class, BOOLEAN_MARSHALLER);
        GENERIC_MARSHALLERS.put(Byte.class, BYTE_MARSHALLER);
        GENERIC_MARSHALLERS.put(Short.class, SHORT_MARSHALLER);
        GENERIC_MARSHALLERS.put(Integer.class, INTEGER_MARSHALLER);
        GENERIC_MARSHALLERS.put(Long.class, LONG_MARSHALLER);
        GENERIC_MARSHALLERS.put(Float.class, FLOAT_MARSHALLER);
        GENERIC_MARSHALLERS.put(Double.class, DOUBLE_MARSHALLER);
        GENERIC_MARSHALLERS.put(String.class, STRING_MARSHALLER);
    }

    @SuppressWarnings("unchecked")
    public static <T> Marshaller<T> get(Class<T> marshalledType) {
        return (Marshaller<T>) GENERIC_MARSHALLERS.get(marshalledType);
    }
}
