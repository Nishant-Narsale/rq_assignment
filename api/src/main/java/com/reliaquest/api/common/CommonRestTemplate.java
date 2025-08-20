package com.reliaquest.api.common;

import org.springframework.web.client.RestTemplate;

/**
 * Reason to use Common RestTemplate object
 * CommonRestTemplate provides a singleton instance of RestTemplate for making API calls.
 * Using this makes sure that we aren't creating new instances of RestTemplate when it can be done
 * using single instance and avoid unnecessary memory usage.
 * @implNote Use CommonRestTemplate.getRestTemplate() to obtain the RestTemplate instance.
 */
public class CommonRestTemplate {
    private static final RestTemplate restTemplate = new RestTemplate();

    public static RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
