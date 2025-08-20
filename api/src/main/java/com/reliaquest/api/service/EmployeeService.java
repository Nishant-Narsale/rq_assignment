package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.common.CommonRestTemplate;
import com.reliaquest.api.model.CreateEmployeeDTO;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;
import com.reliaquest.api.common.CommonObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

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
                logger.info("No employees found in response");
                return Collections.emptyList();
            }
            List<Employee> employees = ((List<?>) dataObj).stream()
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
            logger.info("Fetched {} employees", employees.size());
            return employees;
        } catch (Exception e) {
            logger.error("Error fetching all employees", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error casting response", e);
        }
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        logger.debug("Searching employees by name fragment: {}", searchString);
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(mockApiUrl, Map.class);
            Map<String, Object> body = (Map<String, Object>) response.getBody();
            Object dataObj = body.get("data");
            if (dataObj == null) {
                logger.info("No employees found for search: {}", searchString);
                return Collections.emptyList();
            }
            List<Employee> employees = ((List<?>) dataObj).stream()
                .map(item -> {
                    if (item instanceof Map) {
                        return objectMapper.convertValue(item, Employee.class);
                    } else {
                        logger.error("Item is not a Map: {}", item);
                        return null;
                    }
                })
                .filter(e -> e != null && e.getName() != null && e.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
            logger.info("Found {} employees matching search: {}", employees.size(), searchString);
            return employees;
        } catch (Exception e) {
            logger.error("Error searching employees by name", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error casting response", e);
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
                logger.info("No employee found for ID: {}", id);
                return null;
            }
            Employee employee = objectMapper.convertValue(dataObj, Employee.class);
            logger.info("Fetched employee: {}", employee);
            return employee;
        } catch (Exception e) {
            logger.error("Error fetching employee by ID: {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error casting response", e);
        }
    }

    public int getHighestSalaryOfEmployees() {
        logger.debug("Fetching highest salary among employees");
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(mockApiUrl, Map.class);
            Map<String, Object> body = (Map<String, Object>) response.getBody();
            Object dataObj = body.get("data");
            if (dataObj == null) {
                logger.info("No employees found for salary calculation");
                // TODO: Handle no employees case
                return 0;
            }
            int highestSalary = ((List<?>) dataObj).stream()
                .map(item -> {
                    if (item instanceof Map) {
                        return objectMapper.convertValue(item, Employee.class);
                    } else {
                        logger.error("Item is not a Map: {}", item);
                        return null;
                    }
                })
                .filter(e -> e != null)
                .mapToInt(Employee::getSalary)
                .max()
                .orElse(0);
            logger.info("Highest salary found: {}", highestSalary);
            return highestSalary;
        } catch (Exception e) {
            logger.error("Error fetching highest salary", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error casting response", e);
        }
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(mockApiUrl, Map.class);
            Map<String, Object> body = (Map<String, Object>) response.getBody();
            Object dataObj = body.get("data");
            if (dataObj == null) return Collections.emptyList();
            return ((List<?>) dataObj).stream()
                .map(item -> {
                    if (item instanceof Map) {
                        return objectMapper.convertValue(item, Employee.class);
                    } else {
                        logger.error("Item is not a Map: {}", item);
                        return null;
                    }
                })
                .filter(e -> e != null)
                .sorted((a, b) -> Integer.compare(b.getSalary(), a.getSalary()))
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting top ten highest earning employee names", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error casting response", e);
        }
    }

    public Employee createEmployee(CreateEmployeeDTO employeeInput) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> body = Map.of(
                "name", employeeInput.getName(),
                "salary", employeeInput.getSalary(),
                "age", employeeInput.getAge(),
                "title", employeeInput.getTitle()
            );
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(mockApiUrl, request, Map.class);
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            Object dataObj = responseBody.get("data");
            if (dataObj == null) return null;
            return objectMapper.convertValue(dataObj, Employee.class);
        } catch (Exception e) {
            logger.error("Error creating object", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error casting response", e);
        }
    }

    public String deleteEmployeeById(String id) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> body = Map.of("name", id);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                mockApiUrl,
                org.springframework.http.HttpMethod.DELETE,
                request,
                Map.class
            );
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            Object dataObj = responseBody != null ? responseBody.get("data") : null;
            if (dataObj != null && Boolean.TRUE.equals(dataObj)) {
                logger.info("Successfully deleted employee with id: {}", id);
                return id;
            } else {
                logger.error("Failed to delete employee with id: {}", id);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete employee");
            }
        } catch (Exception e) {
            logger.error("Failed to delete employee with id: {}", id);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete employee", e);
        }
    }
}
