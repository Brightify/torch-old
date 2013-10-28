package com.brightgestures.brightify.model;

import com.brightgestures.brightify.annotation.Entity;

import java.util.List;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
@Entity
public class TableDetails {

    private Long id;
    private List<String> columns;

}
