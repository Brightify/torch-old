package org.brightify.torch.action.load.raw;

import org.brightify.torch.ReadableRawEntity;

import java.util.List;

public interface RawEntityLoader<ENTITY> {

    ReadableRawEntity single();

    List<ReadableRawEntity> list();

}
