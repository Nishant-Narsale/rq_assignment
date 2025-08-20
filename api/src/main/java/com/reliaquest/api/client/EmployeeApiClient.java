package com.reliaquest.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.common.CommonObjectMapper;
import com.reliaquest.api.common.CommonRestTemplate;
import com.reliaquest.api.model.CreateEmployeeDTO;
import com.reliaquest.api.model.Employee;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class EmployeeApiClient {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeApiClient.class);

    @Value("${mock.api.url}")
    private String mockApiUrl;

    private final RestTemplate restTemplate = CommonRestTemplate.getRestTemplate();
    private final ObjectMapper objectMapper = CommonObjectMapper.getObjectMapper();

    public List<Employee> getAllEmployees() {
        logger.debug("Fetching all employees from {}", mockApiUrl);
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(mockApiUrl, Map.class);
            Map<String, Object> body = (Map<String, Object>) response.getBody();
            Object dataObj = body.get("data");
            if (dataObj == null) {
                logger.debug("No employees found in response");
                return Collections.emptyList();
            }
            List<Employee> employees = ((List<?>) dataObj)
                    .stream()
                            .map(item -> {
                                if (item instanceof Map) {
                                    return objectMapper.convertValue(item, Employee.class);
                                } else {
                                    logger.error("Item is not a Map: {}", item);
                                    return null;
                                }
                            })
                            .filter(e -> e != null)
                            .collect(Collectors.toList());
            logger.debug("Fetched {} employees", employees.size());
            return employees;

        } catch (HttpClientErrorException e) {
            logger.error("Client error fetching all employees: {}", e.getStatusCode());
            throw new ApiClientException(
                    "Client error fetching all employees", e, e.getStatusCode().value());
        } catch (HttpServerErrorException e) {
            logger.error("Server error fetching all employees: {}", e.getStatusCode());
            throw new ApiClientException(
                    "Server error fetching all employees", e, e.getStatusCode().value());
        } catch (Exception e) {
            logger.error("Unexpected error fetching all employees", e);
            throw new ApiClientException("Unexpected error fetching all employees", e, 500);
        }
    }

    public Employee getEmployeeById(String id) {
        logger.debug("Fetching employee by ID: {}", id);
        try {
            String url = mockApiUrl + "/" + id;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = (Map<String, Object>) response.getBody();
            Object dataObj = body.get("data");
            if (dataObj == null) {
                logger.debug("No employee found for ID: {}", id);
                throw new ApiClientException("Employee not found", null, 404);
            }
            Employee employee = objectMapper.convertValue(dataObj, Employee.class);
            logger.debug("Fetched employee: {}", employee);
            return employee;

        } catch (HttpClientErrorException e) {
            logger.error("Client error fetching employee by ID: {} - {}", id, e.getStatusCode());
            throw new ApiClientException(
                    "Client error fetching employee by ID", e, e.getStatusCode().value());
        } catch (HttpServerErrorException e) {
            logger.error("Server error fetching employee by ID: {} - {}", id, e.getStatusCode());
            throw new ApiClientException(
                    "Server error fetching employee by ID", e, e.getStatusCode().value());
        } catch (ApiClientException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error fetching employee by ID: {}", id, e);
            throw new ApiClientException("Unexpected error fetching employee by ID", e, 500);
        }
    }

    public Employee createEmployee(CreateEmployeeDTO employeeInput) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CreateEmployeeDTO> request = new HttpEntity<>(employeeInput, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(mockApiUrl, request, Map.class);
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            Object dataObj = responseBody.get("data");
            if (dataObj == null) throw new ApiClientException("Failed to create employee", null, 400);
            Employee employee = objectMapper.convertValue(dataObj, Employee.class);
            logger.debug("Created employee: {}", employee);
            return employee;

        } catch (HttpClientErrorException e) {
            logger.error("Client error creating employee: {}", e.getStatusCode());
            throw new ApiClientException(
                    "Client error creating employee", e, e.getStatusCode().value());
        } catch (HttpServerErrorException e) {
            logger.error("Server error creating employee: {}", e.getStatusCode());
            throw new ApiClientException(
                    "Server error creating employee", e, e.getStatusCode().value());
        } catch (ApiClientException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating employee", e);
            throw new ApiClientException("Unexpected error creating employee", e, 500);
        }
    }

    public String deleteEmployeeByName(String name) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> body = Map.of("name", name);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(mockApiUrl, HttpMethod.DELETE, request, Map.class);
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            Object dataObj = responseBody != null ? responseBody.get("data") : null;
            if (dataObj != null && Boolean.TRUE.equals(dataObj)) {
                logger.debug("Successfully deleted employee with name: {}", name);
                return name;
            } else {
                logger.error("Failed to delete employee with name: {}", name);
                throw new ApiClientException("Failed to delete employee", null, 500);
            }

        } catch (HttpClientErrorException e) {
            logger.error("Client error deleting employee: {} - {}", name, e.getStatusCode());
            throw new ApiClientException(
                    "Client error deleting employee", e, e.getStatusCode().value());
        } catch (HttpServerErrorException e) {
            logger.error("Server error deleting employee: {} - {}", name, e.getStatusCode());
            throw new ApiClientException(
                    "Server error deleting employee", e, e.getStatusCode().value());
        } catch (ApiClientException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error deleting employee: {}", name, e);
            throw new ApiClientException("Unexpected error deleting employee", e, 500);
        }
    }
}
