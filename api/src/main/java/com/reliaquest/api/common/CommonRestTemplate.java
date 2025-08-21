package com.reliaquest.api.common;

import java.io.IOException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Reason to use Common RestTemplate object
 * CommonRestTemplate provides a singleton instance of RestTemplate for making API calls.
 * Using this makes sure that we aren't creating new instances of RestTemplate when it can be done
 * using single instance and avoid unnecessary memory usage.
 * @implNote Use CommonRestTemplate.getRestTemplate() to obtain the RestTemplate instance.
 */
@Component
public class CommonRestTemplate {
    private static final Logger logger = LoggerFactory.getLogger(CommonRestTemplate.class);

    private static final RestTemplate restTemplate;

    @Value("${rest.template.use.retry.for.429:false}")
    private static boolean useRetry;

    static {
        // Connection manager with pooling
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(50); // Total max connections
        connectionManager.setDefaultMaxPerRoute(20); // Max per host

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .evictIdleConnections(Timeout.ofSeconds(30))
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(5000); // 5 sec
        requestFactory.setConnectionRequestTimeout(5000); // 5 sec

        restTemplate = new RestTemplate(requestFactory);

        // Add retry interceptor for 429 responses if enabled
        if (useRetry) {
            restTemplate.getInterceptors().add(new RetryOn429Interceptor(2));
        }
    }

    public static RestTemplate getRestTemplate() {
        return restTemplate;
    }

    /**
     * Interceptor to retry requests on 429 TOO_MANY_REQUESTS to deal with rate-limiting on mock api
     */
    static class RetryOn429Interceptor implements ClientHttpRequestInterceptor {
        private final int maxRetries;
        private final int RETRY_AFTER_SECONDS = 80;

        public RetryOn429Interceptor(int maxRetries) {
            this.maxRetries = maxRetries;
        }

        @Override
        public ClientHttpResponse intercept(
                org.springframework.http.HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            int attempt = 0;
            ClientHttpResponse response = execution.execute(request, body);

            int retryAfterSeconds = RETRY_AFTER_SECONDS / 2;
            while (attempt < maxRetries && response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                attempt++;
                // Get retry-after header (in seconds)
                // Not available in this mock apis case, so it'll always fallback to default 2 seconds
                String retryAfterHeader = response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER);
                retryAfterSeconds = retryAfterSeconds * 2; // exponential retry-delay
                if (retryAfterHeader != null) {
                    try {
                        retryAfterSeconds = Integer.parseInt(retryAfterHeader);
                    } catch (NumberFormatException e) {
                        // fallback to default
                    }
                }
                if (!Thread.currentThread().isInterrupted()) {
                    try {
                        logger.info("Sleeping for {} seconds due to 429", retryAfterSeconds);
                        Thread.currentThread().sleep(retryAfterSeconds * 1000L);
                        logger.debug("Woke up after sleep");
                    } catch (InterruptedException e) {
                        logger.error("Interrupted during sleep!", e);
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    logger.warn("Thread was already interrupted, skipping sleep.");
                    break;
                }
                response = execution.execute(request, body);
            }
            return response;
        }
    }
}
