package org.brightify.torch.sql;

import org.brightify.torch.sql.SqlQueryPart;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public abstract class QueryStatement implements SqlQueryPart {

    public String toSQLString() {
        StringBuilder builder = new StringBuilder();

        query(builder);

        return builder.toString();
    }

}
