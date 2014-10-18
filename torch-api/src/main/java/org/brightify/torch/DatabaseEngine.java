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

    <ENTITY> Map<ENTITY, Long> save(Iterable<ENTITY> entities);

    <ENTITY> Map<ENTITY, Boolean> delete(Iterable<ENTITY> entities);

    <ENTITY> MigrationAssistant<ENTITY> getMigrationAssistant(EntityDescription<ENTITY> metadata);

    TorchFactory getTorchFactory();

    void setTorchFactory(TorchFactory factory);

    boolean wipe();

}
