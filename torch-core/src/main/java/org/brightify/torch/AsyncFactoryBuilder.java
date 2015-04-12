package org.brightify.torch;

import org.brightify.torch.util.async.AsyncRunner;
import org.brightify.torch.util.async.Callback;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class AsyncFactoryBuilder implements AsyncInitializer, AsyncEntityRegistrar, AsyncSubmit,
        AsyncEntityRegistrarSubmit {

    private Callback<TorchFactory> callback;
    private DatabaseEngine databaseEngine;
    private boolean submitted;
    private Set<EntityDescription<?>> metadatas = new LinkedHashSet<EntityDescription<?>>();

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
    public AsyncEntityRegistrarSubmit register(Class<?> entityClass) {
        EntityDescription<?> metadata = EntitiesImpl.resolveEntityDescription(entityClass);
        metadatas.add(metadata);
        return this;
    }

    @Override
    public <ENTITY> AsyncEntityRegistrarSubmit register(EntityDescription<ENTITY> metadata) {
        metadatas.add(metadata);
        return this;
    }

    @Override
    public void submit() {
        submitted = true;

        AsyncRunner.submit(callback, new Callable<TorchFactory>() {
            @Override
            public TorchFactory call() throws Exception {
                return new TorchFactoryImpl(databaseEngine, metadatas);
            }
        });
    }
}
