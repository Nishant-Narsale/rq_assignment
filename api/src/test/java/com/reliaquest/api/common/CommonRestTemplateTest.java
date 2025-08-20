package com.reliaquest.api.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class CommonRestTemplateTest {
    @Test
    void testGetRestTemplateReturnsSingleton() {
        RestTemplate rt1 = CommonRestTemplate.getRestTemplate();
        RestTemplate rt2 = CommonRestTemplate.getRestTemplate();
        assertNotNull(rt1);
        assertNotNull(rt2);
        assertSame(rt1, rt2, "RestTemplate should be singleton");
    }
}
