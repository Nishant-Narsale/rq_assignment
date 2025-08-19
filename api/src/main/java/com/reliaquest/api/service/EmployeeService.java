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

@Service
public class EmployeeService {
    @Value("${mock.api.url}")
    private String mockApiUrl;

    private final RestTemplate restTemplate = CommonRestTemplate.getRestTemplate();
    private final ObjectMapper objectMapper = CommonObjectMapper.getObjectMapper();

    public List<Employee> getAllEmployees() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(mockApiUrl, Map.class);
            Map<String, Object> body = (Map<String, Object>) response.getBody();
            Object dataObj = body.get("data");
            if (dataObj == null) return Collections.emptyList();
            List<Employee> employees = ((List<?>) dataObj).stream()
                .map(item -> objectMapper.convertValue(item, Employee.class))
                .collect(Collectors.toList());
            return employees;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error casting response", e);
        }
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(mockApiUrl, Map.class);
            Map<String, Object> body = (Map<String, Object>) response.getBody();
            Object dataObj = body.get("data");
            if (dataObj == null) return Collections.emptyList();
            List<Employee> employees = ((List<?>) dataObj).stream()
                .map(item -> objectMapper.convertValue(item, Employee.class))
                .filter(e -> e.getName() != null && e.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
            return employees;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error casting response", e);
        }
    }

    public Employee getEmployeeById(String id) {
        try {
            String url = mockApiUrl + "/" + id;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = (Map<String, Object>) response.getBody();
            Object dataObj = body.get("data");
            if (dataObj == null) return null;
            return objectMapper.convertValue(dataObj, Employee.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error casting response", e);
        }
    }

    public int getHighestSalaryOfEmployees() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(mockApiUrl, Map.class);
            Map<String, Object> body = (Map<String, Object>) response.getBody();
            Object dataObj = body.get("data");
            if (dataObj == null) return 0;
            return ((List<?>) dataObj).stream()
                .map(item -> objectMapper.convertValue(item, Employee.class))
                .mapToInt(Employee::getSalary)
                .max()
                .orElse(0);
        } catch (Exception e) {
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
                .map(item -> objectMapper.convertValue(item, Employee.class))
                .sorted((a, b) -> Integer.compare(b.getSalary(), a.getSalary()))
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
        } catch (Exception e) {
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
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error casting response", e);
        }
    }

    public String deleteEmployeeById(String id) {
        try {
            String url = mockApiUrl + "/" + id;
            restTemplate.delete(url);
            return id;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting employee", e);
        }
    }
}
