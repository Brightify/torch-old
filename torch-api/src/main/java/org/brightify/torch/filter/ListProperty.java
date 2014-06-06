package org.brightify.torch.filter;

import org.brightify.torch.util.KeyValueEntity;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ListProperty<PARENT, T> extends Property<T> {

    EntityFilter contains(T... values);

    EntityFilter excludes(T... values);

}
