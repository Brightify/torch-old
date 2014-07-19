package org.brightify.torch;

import org.brightify.torch.util.AsyncRunner;
import org.brightify.torch.util.Callback;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class AsyncFactoryBuilder implements AsyncInitializer, AsyncEntityRegistrar, AsyncSubmit,
        AsyncEntityRegistrarSubmit {

    private Callback<TorchFactory> callback;
    private DatabaseEngine databaseEngine;
    private boolean submitted;
    private Set<EntityMetadata<?>> metadatas = new LinkedHashSet<EntityMetadata<?>>();

    AsyncFactoryBuilder(Callback<TorchFactory> callback) {
        this.callback = callback;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    @Override
    public AsyncEntityRegistrarSubmit with(DatabaseEngine databaseEngine) {
        this.databaseEngine = databaseEngine;
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

        AsyncRunner.submit(callback, new AsyncRunner.Task<TorchFactory>() {
            @Override
            public TorchFactory doWork() throws Exception {
                return new TorchFactoryImpl(databaseEngine, metadatas);
            }
        });
    }
}
