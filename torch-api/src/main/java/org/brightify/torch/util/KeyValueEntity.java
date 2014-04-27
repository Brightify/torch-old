package org.brightify.torch.util;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface KeyValueEntity<KEY, VALUE> {

    KEY getKey();

    void setKey(KEY key);

    VALUE getValue();

    void setValue(KEY value);

}
