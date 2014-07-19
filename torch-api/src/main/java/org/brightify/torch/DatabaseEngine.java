package org.brightify.torch;

import org.brightify.torch.action.load.LoadQuery;
import org.brightify.torch.util.MigrationAssistant;

import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface DatabaseEngine {

    <ENTITY> Iterator<ENTITY> load(LoadQuery<ENTITY> loadQuery);

    <ENTITY> int count(LoadQuery<ENTITY> loadQuery);

    <ENTITY> Map<Key<ENTITY>, ENTITY> save(Iterable<ENTITY> entities);

    <ENTITY> Map<Key<ENTITY>, Boolean> delete(Iterable<Key<ENTITY>> keys);

    <ENTITY> MigrationAssistant<ENTITY> getMigrationAssistant(EntityMetadata<ENTITY> metadata);

    void setTorchFactory(TorchFactory factory);

    boolean wipe();

}
