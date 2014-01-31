package org.brightify.torch;

import android.content.Context;
import org.brightify.torch.util.AsyncRunner;
import org.brightify.torch.util.Callback;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class AsyncFactoryBuilder implements AsyncInitializer, AsyncEntityRegistrar, AsyncSubmit,
        AsyncEntityRegistrarSubmit {

    private Callback<TorchFactory> callback;
    private Context context;
    private boolean submitted;
    private Set<EntityMetadata<?>> metadatas = new LinkedHashSet<EntityMetadata<?>>();

    AsyncFactoryBuilder(Callback<TorchFactory> callback) {
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

        AsyncRunner.run(new AsyncRunner.Task<TorchFactory>() {
            @Override
            public TorchFactory doWork() throws Exception {
                TorchFactory factory = new TorchFactoryImpl(context, metadatas);

                factory.forceOpenOrCreateDatabase();

                return factory;
            }
        }, callback);
    }
}
