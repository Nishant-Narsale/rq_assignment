package com.reliaquest.api.common;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class CommonObjectMapperTest {
    @Test
    void testGetObjectMapperReturnsSingleton() {
        ObjectMapper om1 = CommonObjectMapper.getObjectMapper();
        ObjectMapper om2 = CommonObjectMapper.getObjectMapper();
        assertNotNull(om1);
        assertNotNull(om2);
        assertSame(om1, om2, "ObjectMapper should be singleton");
    }
}
