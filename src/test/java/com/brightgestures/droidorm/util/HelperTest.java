package com.brightgestures.droidorm.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class HelperTest {

    @Test
    public void testDeCamelize() {
        assertEquals("camel_case", Helper.deCamelize("CamelCase"));
        assertEquals("camel_camel_case", Helper.deCamelize("CamelCamelCase"));
        assertEquals("camel2_camel2_case", Helper.deCamelize("Camel2Camel2Case"));
        assertEquals("get_http_response_code", Helper.deCamelize("getHTTPResponseCode"));
        assertEquals("get2_http_response_code", Helper.deCamelize("get2HTTPResponseCode"));
        assertEquals("http_response_code", Helper.deCamelize("HTTPResponseCode"));
        assertEquals("http_response_code_xyz", Helper.deCamelize("HTTPResponseCodeXYZ"));
    }
}
