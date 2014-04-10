package org.brightify.torch.sql.statement;

import org.brightify.torch.sql.Statement;
import org.brightify.torch.sql.util.TextUtils;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class DropTable extends Statement {

    protected boolean ifExists = true;
    protected String databaseName = null;
    protected String tableName;

    public boolean isIfExists() {
        return ifExists;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void query(StringBuilder builder) {
        if(TextUtils.isEmpty(tableName)) {
            throw new IllegalStateException("Table name cannot be null or empty!");
        }

        builder.append("DROP TABLE ");

        if(ifExists) {
            builder.append("IF EXISTS ");
        }

        if(!TextUtils.isEmpty(databaseName)) {
            builder.append(databaseName).append(".");
        }

        builder.append(tableName);
    }
}
