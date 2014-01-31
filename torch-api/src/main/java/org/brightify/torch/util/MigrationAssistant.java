package org.brightify.torch.util;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public interface MigrationAssistant<ENTITY> {

    void createTable();

    void dropTable();

    void dropCreateTable();

    boolean tableExists();

}
