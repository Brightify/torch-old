package org.brightify.torch.compile.feature;

import com.sun.codemodel.JExpression;
import org.brightify.torch.compile.PropertyMirror;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface FeatureProviderRegistry {

    List<JExpression> getFeatureConstructions(PropertyMirror propertyMirror);

}
