package com.brightgestures.brightify;

import android.content.Context;
import com.brightgestures.brightify.util.AsyncRunner;
import com.brightgestures.brightify.util.Callback;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class AsyncFactoryBuilder implements AsyncInitializer, AsyncEntityRegistrar, AsyncSubmit,
        AsyncEntityRegistrarSubmit {

    private Callback<BrightifyFactory> callback;
    private Context context;
    private boolean submitted;
    private Set<EntityMetadata<?>> metadatas = new LinkedHashSet<EntityMetadata<?>>();

    AsyncFactoryBuilder(Callback<BrightifyFactory> callback) {
        this.callback = callback;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    @Override
    public AsyncEntityRegistrarSubmit with(Context context) {
        this.context = context.getApplicationContext();
        return this;
    }

    @Override
    public <ENTITY> AsyncEntityRegistrarSubmit register(Class<ENTITY> entityClass) {
        EntityMetadata<ENTITY> metadata = EntitiesImpl.findMetadata(entityClass);
        metadatas.add(metadata);
        return this;
    }

    @Override
    public <ENTITY> AsyncEntityRegistrarSubmit register(EntityMetadata<ENTITY> metadata) {
        metadatas.add(metadata);
        return this;
    }

    @Override
    public void submit() {
        submitted = true;

        AsyncRunner.run(new AsyncRunner.Task<BrightifyFactory>() {
            @Override
            public BrightifyFactory doWork() throws Exception {
                BrightifyFactory factory = new BrightifyFactoryImpl(context, metadatas);

                factory.forceOpenOrCreateDatabase();

                return factory;
            }
        }, callback);
    }
}
