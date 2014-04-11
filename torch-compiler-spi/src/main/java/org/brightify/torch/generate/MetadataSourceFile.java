package org.brightify.torch.generate;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface MetadataSourceFile {

    public static final String CURSOR = "cursor";
    public static final String ENTITY = "entity";
    public static final String CONTENT_VALUES = "values";

    public MetadataSourceFile line(Object object);

    public MetadataSourceFile append(Object object);

}
