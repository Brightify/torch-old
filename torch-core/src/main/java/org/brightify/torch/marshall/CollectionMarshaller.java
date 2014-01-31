package org.brightify.torch.marshall;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class CollectionMarshaller {

    public static <ITEM> void unmarshall(DataInputStream dataInputStream, Collection<ITEM> collection,
                                      Marshaller<ITEM> itemMarshaller) throws IOException {
        int count = dataInputStream.readInt();
        for(int i = 0; i < count; i++) {
            ITEM item = itemMarshaller.unmarshall(dataInputStream);
            collection.add(item);
        }
    }

    public static <ITEM> void marshall(DataOutputStream dataOutputStream, Collection<ITEM> collection,
                                    Marshaller<ITEM> itemMarshaller) throws IOException {
        dataOutputStream.writeInt(collection.size());
        for(ITEM item : collection) {
            itemMarshaller.marshall(dataOutputStream, item);
        }
    }

}
