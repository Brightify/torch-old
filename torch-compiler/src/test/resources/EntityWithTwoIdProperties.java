import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;

@Entity
public class EntityWithTwoIdProperties {

    @Id
    Long id1;

    @Id
    Long id2;

}