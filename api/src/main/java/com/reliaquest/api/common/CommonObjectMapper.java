package com.reliaquest.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Reason to use Common ObjectMapper object
 * CommonObjectMapper provides a singleton instance of ObjectMapper for JSON serialization/deserialization.
 * Using this makes sure that we aren't creating new instances of ObjectMapper when it can be done
 * using single instance and avoid unnecessary memory usage.
 * ObjectMapper size can be large and it's thread-safe, so we can single ObjectMapper instance.
 * @implNote Use CommonObjectMapper.getObjectMapper() to obtain the ObjectMapper instance.
 */
public class CommonObjectMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
