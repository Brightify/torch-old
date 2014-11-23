package org.brightify.torch.action.load.raw;

import java.util.List;

public interface RawPropertyLoader<ENTITY, VALUE> {

    VALUE single();

    List<VALUE> list();

}
