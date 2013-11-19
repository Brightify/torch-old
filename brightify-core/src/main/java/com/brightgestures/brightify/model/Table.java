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

    @Id
    public Long id;
    public String tableName;
    public Integer version;
    public Key<TableDetails> details;
    public Key uglyKey;
    public List<Key<TableDetails>> detailList;
}
