package com.brightgestures.brightify.action.load;

import android.database.Cursor;
import android.os.CancellationSignal;
import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.Entities;
import com.brightgestures.brightify.EntityMetadata;
import com.brightgestures.brightify.Property;
import com.brightgestures.brightify.action.load.impl.FilterLoaderImpl;
import com.brightgestures.brightify.sql.statement.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class LoadQuery<E> {
    protected final Brightify mBrightify;
    protected final EntityMetadata<E> mEntityMetadata;

    protected LinkedList<Class<?>> mLoadGroups = new LinkedList<Class<?>>();
    protected LinkedList<FilterLoaderImpl.FilterType> mFilterTypes = new LinkedList<FilterLoaderImpl.FilterType>();
    protected ArrayList<String> mConditionArgs = new ArrayList<String>();

    private LoadQuery(Brightify brightify, EntityMetadata<E> entityMetadata) {
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

    public Class<E> getEntityClass() {
        return mEntityMetadata.getEntityClass();
    }

    public EntityMetadata<E> getEntityMetadata() {
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
        builder.append(";");

        String sql = builder.toString();
        String[] selectionArgs = mConditionArgs.toArray(new String[mConditionArgs.size()]);

        return mBrightify.getReadableDatabase().rawQuery(sql, selectionArgs);
    }

    public static class Builder {

        public static <T extends BaseLoader<E> & GenericLoader<E>, E> LoadQuery<E> build(T lastLoader) {
            Class<E> entityType = lastLoader.getType();
            EntityMetadata<E> entityMetadata = Entities.getMetadata(entityType);
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
