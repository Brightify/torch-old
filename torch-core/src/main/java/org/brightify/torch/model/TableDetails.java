package org.brightify.torch.model;

import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;

import java.util.List;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
@Entity
public class TableDetails {

    @Id
    public Long id;
    public List<String> columns;

}
