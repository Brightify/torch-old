package org.brightify.torch.android.internal;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class SQLiteMaster {

    private String type;
    private String name;
    private String tableName;
    private int rootpage;
    private String sql;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getRootpage() {
        return rootpage;
    }

    public void setRootpage(int rootpage) {
        this.rootpage = rootpage;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

}
