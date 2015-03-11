package negative;

import org.brightify.torch.annotation.Id;
import org.brightify.torch.annotation.Entity;

@Entity
public class EntityWithPrimitiveIntId {
    @Id
    int id;
}