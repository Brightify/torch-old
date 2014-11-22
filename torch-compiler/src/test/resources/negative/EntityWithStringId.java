package negative;

import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;

@Entity
public class EntityWithStringId {

    @Id
    String id;

}