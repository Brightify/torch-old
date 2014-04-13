package org.brightify.torch.compile.generate;

import com.sun.codemodel.JCodeModel;
import org.brightify.torch.compile.EntityInfo;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface EntityMetadataGenerator {

    JCodeModel generate(EntityInfo entityInfo) throws Exception;

}
