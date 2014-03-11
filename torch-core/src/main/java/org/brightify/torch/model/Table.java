package org.brightify.torch.model;

import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
@Entity
public class Table {

    private Long id;
    private String tableName;
    private String version;

    // FIXME if no generic type argument is set, handle error through messager
    // public Key<TableDetails> details;
    // public Key uglyKey;
    // public List<Key<TableDetails>> detailList;

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
