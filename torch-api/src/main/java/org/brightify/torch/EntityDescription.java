package org.brightify.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface EntityDescription {

    String[] getProperties();

    Class<?>[] getPropertyTypes();

}
