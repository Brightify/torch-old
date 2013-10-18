package com.brightgestures.brightify.generate;

import com.brightgestures.brightify.SourceFileGenerator;
import com.brightgestures.brightify.parse.EntityInfo;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Set;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class EntitiesGenerator extends SourceFileGenerator {
    public EntitiesGenerator(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }

    public void generateEntities(Set<EntityInfo> entityInfoSet) {

    }
}
