package org.brightify.torch;

import org.brightify.torch.action.load.LoadQuery;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface EntityManager {

    <ENTITY> List<ENTITY> load(LoadQuery<ENTITY> query);

    <ENTITY> ENTITY first(LoadQuery<ENTITY> query);

    <ENTITY> int count(LoadQuery<ENTITY> query);

    <ENTITY> Map<ENTITY, Long> save(Iterable<ENTITY> entities);

    <ENTITY> Map<ENTITY, Boolean> delete(Iterable<ENTITY> entities);

}
