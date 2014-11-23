package references.list;

import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;

import java.util.List;

@Entity
public class ParentEntity {

    @Id
    Long id;

    String name;

    List<ChildEntity> childEntities;

}