package com.brightgestures.brightify.model;

import com.brightgestures.brightify.Ref;
import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.annotation.Id;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@Entity
public class Table {

    @Id
    public Long id;
    public String tableName;
    public Integer version;
}
