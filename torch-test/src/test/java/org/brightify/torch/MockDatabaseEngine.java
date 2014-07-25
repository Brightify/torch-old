package org.brightify.torch;

import org.brightify.torch.action.load.LoadQuery;
import org.brightify.torch.util.MigrationAssistant;

import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class MockDatabaseEngine implements DatabaseEngine {
    @Override
    public <ENTITY> Iterator<ENTITY> load(LoadQuery<ENTITY> loadQuery) {
        return null;
    }

    @Override
    public <ENTITY> int count(LoadQuery<ENTITY> loadQuery) {
        return 0;
    }

    @Override
    public <ENTITY> Map<Key<ENTITY>, ENTITY> save(Iterable<ENTITY> entities) {
        return null;
    }

    @Override
    public <ENTITY> Map<Key<ENTITY>, Boolean> delete(Iterable<Key<ENTITY>> keys) {
        return null;
    }

    @Override
    public <ENTITY> MigrationAssistant<ENTITY> getMigrationAssistant(EntityDescription<ENTITY> metadata) {
        return null;
    }

    @Override
    public TorchFactory getTorchFactory() {
        return null;
    }

    @Override
    public void setTorchFactory(TorchFactory factory) {

    }

    @Override
    public boolean wipe() {
        return false;
    }
}
