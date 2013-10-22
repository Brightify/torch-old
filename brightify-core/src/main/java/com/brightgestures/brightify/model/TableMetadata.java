package com.brightgestures.brightify.model;

import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.annotation.Id;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@Entity
public class TableMetadata {

    private Long id;
    private String tableName;
    private Integer version;

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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
