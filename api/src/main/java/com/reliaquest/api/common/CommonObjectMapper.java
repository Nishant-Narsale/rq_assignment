package com.reliaquest.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonObjectMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
