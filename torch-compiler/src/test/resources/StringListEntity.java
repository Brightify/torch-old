import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;

import java.util.List;

@Entity
public class StringListEntity {

    @Id
    Long id;

    List<String> strings;

}