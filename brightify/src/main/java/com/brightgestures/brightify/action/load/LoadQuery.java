package com.brightgestures.brightify.action.load;

import android.database.Cursor;
import android.util.Log;
import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.Entities;
import com.brightgestures.brightify.EntityMetadataCompatibility;
import com.brightgestures.brightify.Property;
import com.brightgestures.brightify.action.load.api.GenericLoader;
import com.brightgestures.brightify.action.load.api.OrderLoader;
import com.brightgestures.brightify.action.load.impl.FilterLoaderImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class LoadQuery<E> {
    private static final String TAG = LoadQuery.class.getSimpleName();

    protected final Brightify mBrightify;
    protected final EntityMetadataCompatibility<E> mEntityMetadata;

    protected LinkedList<Class<?>> mLoadGroups = new LinkedList<Class<?>>();
    protected LinkedList<FilterLoaderImpl.FilterType> mFilterTypes = new LinkedList<FilterLoaderImpl.FilterType>();
    protected ArrayList<String> mConditionArgs = new ArrayList<String>();
    protected HashMap<String, OrderLoader.Direction> mOrdering = new HashMap<String, OrderLoader.Direction>();
    protected Integer mLimit;
    protected Integer mOffset;

    private LoadQuery(Brightify brightify, EntityMetadataCompatibility<E> entityMetadata) {
        mBrightify = brightify;
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

    public LoadQuery<E> addConditionArg(String conditionArg) {
        mConditionArgs.add(conditionArg);
        return this;
    }

    public LoadQuery<E> addOrdering(String orderColumn, OrderLoader.Direction direction) {
        if(!mOrdering.containsKey(orderColumn)) {
            mOrdering.put(orderColumn, direction);
        }
        return this;
    }

    public void setLimit(Integer limit) {
        mLimit = limit;
    }

    public void setOffset(Integer offset) {
        mOffset = offset;
    }

    public Class<E> getEntityClass() {
        return mEntityMetadata.getEntityClass();
    }

    public EntityMetadataCompatibility<E> getEntityMetadata() {
        return mEntityMetadata;
    }

    public Cursor run() {
        StringBuilder builder = new StringBuilder();

        builder.append("SELECT ");

        List<Property> properties = mEntityMetadata.getProperties();

        // TODO add some validation of filters
        int i = 0;
        for(Property property : properties) {
            if(i > 0) {
                builder.append(", ");
            }
            builder.append(property.getColumnName());
            i++;
        }

        builder.append(" FROM ").append(mEntityMetadata.getTableName());
        if(mFilterTypes.size() > 0) {
            builder.append(" WHERE ");
            for(FilterLoaderImpl.FilterType filterType : mFilterTypes) {
                builder.append(filterType.toSQL(this));
            }
        }

        if(mOrdering.size() > 0) {
            builder.append(" ORDER BY ");
            for(Map.Entry<String, OrderLoader.Direction> entry : mOrdering.entrySet()) {
                builder.append(entry.getKey()).append(" ")
                       .append(entry.getValue() == OrderLoader.Direction.ASCENDING ? "ASC" : "DESC");
            }
        }

        if(mLimit != null) {
            builder.append(" LIMIT ").append(mLimit);
            if(mOffset != null) {
                builder.append(" OFFSET ").append(mOffset);
            }
        }

        builder.append(";");

        String sql = builder.toString();
        String[] selectionArgs = mConditionArgs.toArray(new String[mConditionArgs.size()]);

        if(mBrightify.getFactory().getConfiguration().isEnableQueryLogging()) {
            Log.d(TAG, sql);
        }

        return mBrightify.getDatabase().rawQuery(sql, selectionArgs);
    }

    public static class Builder {

        public static <T extends BaseLoader<E> & GenericLoader<E>, E> LoadQuery<E> build(T lastLoader) {
            Class<E> entityType = lastLoader.getType();
            EntityMetadataCompatibility<E> entityMetadata = Entities.getMetadata(entityType);
            LoadQuery<E> loadQuery = new LoadQuery<E>(lastLoader.mBrightify, entityMetadata);

            BaseLoader<E> loader = lastLoader;
            while(loader != null) {
                loader.prepareQuery(loadQuery);

                loader = loader.getParentLoader();
            }

            return loadQuery;
        }
    }

}
