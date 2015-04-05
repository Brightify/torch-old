package org.brightify.torch;

import org.brightify.torch.action.load.LoadQuery;
import org.brightify.torch.util.MigrationAssistant;
import org.brightify.torch.util.functional.EditFunction;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface DatabaseEngine {

    TorchFactory getTorchFactory();

    void setTorchFactory(TorchFactory factory);

    boolean open();

    boolean close();

    boolean wipe();

    boolean isOpen();

    <ENTITY> void each(LoadQuery<ENTITY> loadQuery, EditFunction<ENTITY> function);

    <ENTITY> List<ENTITY> load(LoadQuery<ENTITY> loadQuery);

    <ENTITY> ENTITY first(LoadQuery<ENTITY> loadQuery);

    <ENTITY> int count(LoadQuery<ENTITY> loadQuery);

    <ENTITY> Map<ENTITY, Long> save(Iterable<ENTITY> entities);

    <ENTITY> Map<ENTITY, Boolean> delete(Iterable<ENTITY> entities);

    <ENTITY> MigrationAssistant<ENTITY> getMigrationAssistant(EntityDescription<ENTITY> metadata);

}
