package org.brightify.torch.action.load;

import org.brightify.torch.util.async.Callback;
import org.brightify.torch.util.functional.EditFunction;
import org.brightify.torch.util.functional.FoldingFunction;
import org.brightify.torch.util.functional.MappingFunction;

import java.util.List;

/**
 * Utility loader for processing the data. Do not store references to the entities as they will be reused and their
 * contents changed.
 *
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ProcessingLoader<ENTITY> {

    <RESULT> List<RESULT> map(MappingFunction<ENTITY, RESULT> function);

    <RESULT> RESULT fold(FoldingFunction<RESULT, ENTITY> function);

    <RESULT> RESULT fold(RESULT initialValue, FoldingFunction<RESULT, ENTITY> function);

    void each(EditFunction<ENTITY> function);

    <RESULT> void map(MappingFunction<ENTITY, RESULT> function, Callback<List<RESULT>> callback);

    <RESULT> void fold(FoldingFunction<RESULT, ENTITY> function, Callback<RESULT> callback);

    <RESULT> void fold(RESULT initialValue, FoldingFunction<RESULT, ENTITY> function, Callback<RESULT> callback);

    void each(EditFunction<ENTITY> function, Callback<Void> callback);

}
