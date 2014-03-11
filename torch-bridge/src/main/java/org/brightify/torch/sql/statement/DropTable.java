package org.brightify.torch.sql.statement;

import android.text.TextUtils;
import org.brightify.torch.sql.Statement;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class DropTable extends Statement {

    protected boolean mIfExists = true;
    protected String mDatabaseName = null;
    protected String mTableName;

    public boolean isIfExists() {
        return mIfExists;
    }

    public void setIfExists(boolean ifExists) {
        mIfExists = ifExists;
    }

    public String getDatabaseName() {
        return mDatabaseName;
    }

    public void setDatabaseName(String databaseName) {
        mDatabaseName = databaseName;
    }

    public String getTableName() {
        return mTableName;
    }

    public void setTableName(String tableName) {
        mTableName = tableName;
    }

    @Override
    public void query(StringBuilder builder) {
        if(TextUtils.isEmpty(mTableName)) {
            throw new IllegalStateException("Table name cannot be null or empty!");
        }

        builder.append("DROP TABLE ");

        if(mIfExists) {
            builder.append("IF EXISTS ");
        }

        if(!TextUtils.isEmpty(mDatabaseName)) {
            builder.append(mDatabaseName).append(".");
        }

        builder.append(mTableName);
    }
}
