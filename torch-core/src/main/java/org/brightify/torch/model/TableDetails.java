package org.brightify.torch.model;

import org.brightify.torch.Key;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
@Entity
public class TableDetails {

    @Id
    public Long id;
    public Key<Table> tableKey;
//    public List<String> columns;

}
