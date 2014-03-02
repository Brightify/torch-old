package org.brightify.torch.action.save;

import org.brightify.torch.Key;
import org.brightify.torch.Result;
import org.brightify.torch.action.AsyncSelector;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface Saver extends AsyncSelector<AsyncSaver> {

    <ENTITY> Key<ENTITY> entity(ENTITY entity);
    <ENTITY> Map<Key<ENTITY>, ENTITY> entities(ENTITY... entities);
    <ENTITY> Map<Key<ENTITY>, ENTITY> entities(Iterable<ENTITY> entities);




}
