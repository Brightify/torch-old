package org.brightify.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ReadableRawContainer {

    boolean isNull();

    byte[] getBlob();

    Boolean getBoolean();

    Byte getByte();

    Short getShort();

    Integer getInteger();

    Long getLong();

    Double getDouble();

    Float getFloat();

    String getString();

}
