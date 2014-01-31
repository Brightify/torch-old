package org.brightify.torch.marshall.stream;

import org.brightify.torch.marshall.StreamMarshaller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class ArrayListStreamMarshaller<ITEM_TYPE> implements StreamMarshaller<List<ITEM_TYPE>, ArrayList<ITEM_TYPE>> {

    private static final Map<StreamMarshaller<?, ?>, ArrayListStreamMarshaller<?>> instances =
            new HashMap<StreamMarshaller<?, ?>, ArrayListStreamMarshaller<?>>();
    private final StreamMarshaller<ITEM_TYPE, ITEM_TYPE> itemMarshaller;

    private ArrayListStreamMarshaller(StreamMarshaller<ITEM_TYPE, ITEM_TYPE> itemMarshaller) {
        this.itemMarshaller = itemMarshaller;
    }

    @Override
    public void marshall(DataOutputStream outputStream, List<ITEM_TYPE> value) throws Exception {
        outputStream.writeInt(value.size());
        for (ITEM_TYPE item : value) {
            itemMarshaller.marshall(outputStream, item);
        }
    }

    @Override
    public ArrayList<ITEM_TYPE> unmarshall(DataInputStream inputStream) throws Exception {
        ArrayList<ITEM_TYPE> items = new ArrayList<ITEM_TYPE>();
        int count = inputStream.readInt();
        for (int i = 0; i < count; i++) {
            ITEM_TYPE item = itemMarshaller.unmarshall(inputStream);
            items.add(item);
        }
        return items;
    }

    public static <ITEM_TYPE> ArrayListStreamMarshaller<ITEM_TYPE> getInstance(StreamMarshaller<ITEM_TYPE, ITEM_TYPE> itemMarshaller) {
        if (!instances.containsKey(itemMarshaller)) {
            instances.put(itemMarshaller, new ArrayListStreamMarshaller<ITEM_TYPE>(itemMarshaller));
        }
        return (ArrayListStreamMarshaller<ITEM_TYPE>) instances.get(itemMarshaller);
    }
}
