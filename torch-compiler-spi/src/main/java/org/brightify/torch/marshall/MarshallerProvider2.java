package org.brightify.torch.marshall;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface MarshallerProvider2 {

    <T> Marshaller2<T> getMarshaller(Class<T> marshalledClass);

}
