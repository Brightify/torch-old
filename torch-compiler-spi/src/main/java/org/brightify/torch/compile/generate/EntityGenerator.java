package org.brightify.torch.compile.generate;

import com.sun.codemodel.JDefinedClass;
import org.brightify.torch.compile.EntityMirror;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface EntityGenerator {

    void generate(EntityMirror entityMirror) throws Exception;

    void generate(EntityMirror entityMirror, JDefinedClass definedClass) throws Exception;

}
