package com.brightgestures.brightify.model;

import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.annotation.Id;

import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
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
