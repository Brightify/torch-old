package org.brightify.torch.marshall;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class MapMarshaller {

    public static <KEY, VALUE> void unmarshall(DataInputStream dataInputStream, Map<KEY, VALUE> map,
                                               Marshaller<KEY> keyMarshaller, Marshaller<VALUE> valueMarshaller)
            throws IOException {
        int count = dataInputStream.readInt();
        for (int i = 0; i < count; i++) {
            KEY key = keyMarshaller.unmarshall(dataInputStream);
            VALUE value = valueMarshaller.unmarshall(dataInputStream);
            map.put(key, value);
        }
    }

    public static <KEY, VALUE> void marshall(DataOutputStream dataOutputStream, Map<KEY, VALUE> map,
                                             Marshaller<KEY> keyMarshaller, Marshaller<VALUE> valueMarshaller)
            throws IOException {
        dataOutputStream.writeInt(map.size());
        for(KEY key : map.keySet()) {
            keyMarshaller.marshall(dataOutputStream, key);
            valueMarshaller.marshall(dataOutputStream, map.get(key));
        }
    }

}
