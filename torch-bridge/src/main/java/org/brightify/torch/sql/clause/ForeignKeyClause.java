package org.brightify.torch.sql.clause;

import org.brightify.torch.sql.SqlQueryPart;

import java.util.ArrayList;
import java.util.Collection;

public class ForeignKeyClause implements SqlQueryPart {

    protected String mForeignTable;
    protected Collection<String> mColumns = new ArrayList<String>();

    @Override
    public void query(StringBuilder builder) {
        if(true) throw new RuntimeException("Not implemented yet!");
        builder.append("REFERENCES ").append(mForeignTable);
        if(mColumns.size() > 0) {

        }
    }
}
