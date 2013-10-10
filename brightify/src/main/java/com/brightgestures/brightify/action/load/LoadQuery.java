package com.brightgestures.brightify.action.load;

import com.brightgestures.brightify.Entities;
import com.brightgestures.brightify.EntityMetadata;
import com.brightgestures.brightify.action.load.impl.FilterLoaderImpl;

import java.util.Collections;
import java.util.LinkedList;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class LoadQuery<E> {
    protected final EntityMetadata<E> mEntityMetadata;

    protected LinkedList<Class<?>> mLoadGroups = new LinkedList<Class<?>>();
    protected LinkedList<FilterLoaderImpl.FilterType> mFilterTypes = new LinkedList<FilterLoaderImpl.FilterType>();

    public LoadQuery(EntityMetadata<E> entityMetadata) {
        mEntityMetadata = entityMetadata;
    }

    public LoadQuery<E> addLoadGroups(Class<?>... loadGroups) {
        Collections.addAll(mLoadGroups, loadGroups);
        return this;
    }

    public LoadQuery<E> addFilterType(FilterLoaderImpl.FilterType filterType) {
        mFilterTypes.addFirst(filterType);
        return this;
    }

    public Class<E> getEntityClass() {
        return mEntityMetadata.getEntityClass();
    }

    public static class Builder {

        public static <T extends BaseLoader<E> & GenericLoader<E>, E> LoadQuery<E> build(T lastLoader) {
            Class<E> entityType = lastLoader.getType();
            EntityMetadata<E> entityMetadata = Entities.getMetadata(entityType);
            LoadQuery<E> loadQuery = new LoadQuery<E>(entityMetadata);

            BaseLoader<E> loader = lastLoader;
            while(loader != null) {
                loader.prepareQuery(loadQuery);

                loader = loader.getParentLoader();
            }

            return loadQuery;
        }
    }

}
