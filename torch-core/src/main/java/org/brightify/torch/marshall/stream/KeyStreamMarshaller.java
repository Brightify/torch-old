package org.brightify.torch.marshall.stream;

import org.brightify.torch.Key;
import org.brightify.torch.KeyFactory;
import org.brightify.torch.marshall.SymetricStreamMarshaller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class KeyStreamMarshaller<ENTITY> implements SymetricStreamMarshaller<Key<ENTITY>> {

    private static final Map<Class<?>, KeyStreamMarshaller<?>> instances = new HashMap<Class<?>, KeyStreamMarshaller<?>>();
    private final Class<ENTITY> entityClass;

    private KeyStreamMarshaller(Class<ENTITY> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void marshall(DataOutputStream outputStream, Key<ENTITY> value) throws Exception{
        StringStreamMarshaller.getInstance().marshall(outputStream, value.getType().getName());
        LongStreamMarshaller.getInstance().marshall(outputStream, value.getId());
    }

    @Override
    public Key<ENTITY> unmarshall(DataInputStream inputStream) throws Exception {
        String className = StringStreamMarshaller.getInstance().unmarshall(inputStream);
        if(!entityClass.getName().equals(className)) {
            throw new IllegalStateException("Loaded key of different entity class!");
        }

        long id = LongStreamMarshaller.getInstance().unmarshall(inputStream);

        return KeyFactory.create(entityClass, id);
    }

    public static <ENTITY> KeyStreamMarshaller<ENTITY> getInstance(Class<ENTITY> entityClass) {
        if(entityClass == null) {
            throw new IllegalArgumentException("Keys has to have entityClass!");
        }
        if(!instances.containsKey(entityClass)) {
            instances.put(entityClass, new KeyStreamMarshaller<ENTITY>(entityClass));
        }
        return (KeyStreamMarshaller<ENTITY>) instances.get(entityClass);
    }
}
