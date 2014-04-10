package org.brightify.torch.util;

import org.brightify.torch.filter.Column;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface MigrationAssistant<ENTITY> {

    void addColumn(Column<?> column);

    void changeColumnType(Column<?> column, Class<?> from, Class<?> to);

    void renameColumn(String from, String to);

    void removeColumn(String name);



    @Deprecated
    void dropTable();

    @Deprecated
    void createTable();

    @Deprecated
    void dropCreateTable();

    @Deprecated
    boolean tableExists();

}
