package negative;

import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;

@Entity
public class EntityWithPrimitiveLongId {

    @Id
    long id;

}