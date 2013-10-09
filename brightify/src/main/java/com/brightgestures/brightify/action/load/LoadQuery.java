package com.brightgestures.brightify.action.load;

import com.brightgestures.brightify.Entities;
import com.brightgestures.brightify.EntityMetadata;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class LoadQuery<E> {
    protected final EntityMetadata<E> mEntityMetadata;


    public LoadQuery(EntityMetadata<E> entityMetadata) {
        mEntityMetadata = entityMetadata;
    }



    public static class Builder {

        public static <E> LoadQuery<E> build(GenericLoader<E> lastLoader) {
            Class<E> entityType = lastLoader.getType();
            EntityMetadata<E> entityMetadata = Entities.getMetadata(entityType);



            throw new UnsupportedOperationException("Not yet implemented!");
        }
    }

}
