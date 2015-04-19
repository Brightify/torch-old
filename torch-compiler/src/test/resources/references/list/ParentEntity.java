package references.list;

import org.brightify.torch.RefCollection;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;

import java.util.Collection;

@Entity
public class ParentEntity {

    @Id
    Long id;

    String name;

    //RefCollection<ChildEntity> childEntities;
    Collection<ChildEntity> childEntities;

}