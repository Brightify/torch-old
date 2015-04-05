package references.single;

import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;

@Entity
public class ChildEntity {

    @Id
    Long id;

    String name;

}