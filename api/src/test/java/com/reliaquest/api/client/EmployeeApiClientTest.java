package com.reliaquest.api.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

class EmployeeApiClientTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EmployeeApiClient employeeApiClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeApiClient = new EmployeeApiClient();
        // reflection to inject mocks for final fields
        try {
            var restTemplateField = EmployeeApiClient.class.getDeclaredField("restTemplate");
            restTemplateField.setAccessible(true);
            restTemplateField.set(employeeApiClient, restTemplate);
            var objectMapperField = EmployeeApiClient.class.getDeclaredField("objectMapper");
            objectMapperField.setAccessible(true);
            objectMapperField.set(employeeApiClient, objectMapper);
            var urlField = EmployeeApiClient.class.getDeclaredField("mockApiUrl");
            urlField.setAccessible(true);
            urlField.set(employeeApiClient, "http://mock-api");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetAllEmployeesSuccess() {
        Map<String, Object> body = new HashMap<>();
        body.put("data", Collections.emptyList());
        ResponseEntity<Map<String, Object>> response = ResponseEntity.ok(body);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn((ResponseEntity) response);
        List<Employee> result = employeeApiClient.getAllEmployees();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllEmployeesClientError() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(HttpClientErrorException.create(
                        "429 TOO_MANY_REQUESTS",
                        org.springframework.http.HttpStatusCode.valueOf(429),
                        "Too Many Requests",
                        null,
                        null,
                        null));
        ApiClientException ex = assertThrows(ApiClientException.class, () -> employeeApiClient.getAllEmployees());
        assertEquals(429, ex.getStatusCode());
    }

    @Test
    void testGetAllEmployeesServerError() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(HttpServerErrorException.create(
                        "500 INTERNAL_SERVER_ERROR",
                        org.springframework.http.HttpStatusCode.valueOf(500),
                        "Internal Server Error",
                        null,
                        null,
                        null));
        ApiClientException ex = assertThrows(ApiClientException.class, () -> employeeApiClient.getAllEmployees());
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    void testGetEmployeeByIdNotFound() {
        Map<String, Object> body = new HashMap<>();
        body.put("data", null);
        ResponseEntity<Map<String, Object>> response = ResponseEntity.ok(body);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn((ResponseEntity) response);
        ApiClientException ex = assertThrows(ApiClientException.class, () -> employeeApiClient.getEmployeeById("id"));
        assertEquals(404, ex.getStatusCode());
    }

    @Test
    void testCreateEmployeeSuccess() {
        com.reliaquest.api.model.CreateEmployeeDTO dto = new com.reliaquest.api.model.CreateEmployeeDTO();
        Map<String, Object> responseBody = new HashMap<>();
        Employee employee = new Employee();
        responseBody.put("data", new HashMap<>());
        ResponseEntity<Map<String, Object>> response = ResponseEntity.ok(responseBody);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class))).thenReturn((ResponseEntity) response);
        when(objectMapper.convertValue(any(), eq(Employee.class))).thenReturn(employee);
        Employee result = employeeApiClient.createEmployee(dto);
        assertNotNull(result);
    }

    @Test
    void testCreateEmployeeClientError() {
        com.reliaquest.api.model.CreateEmployeeDTO dto = new com.reliaquest.api.model.CreateEmployeeDTO();
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenThrow(HttpClientErrorException.create(
                        "429 TOO_MANY_REQUESTS",
                        org.springframework.http.HttpStatusCode.valueOf(429),
                        "Too Many Requests",
                        null,
                        null,
                        null));
        ApiClientException ex = assertThrows(ApiClientException.class, () -> employeeApiClient.createEmployee(dto));
        assertEquals(429, ex.getStatusCode());
    }

    @Test
    void testDeleteEmployeeByNameSuccess() {
        String name = "John Doe";
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", Boolean.TRUE);
        ResponseEntity<Map<String, Object>> response = ResponseEntity.ok(responseBody);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(), eq(Map.class)))
                .thenReturn((ResponseEntity) response);
        String result = employeeApiClient.deleteEmployeeByName(name);
        assertEquals(name, result);
    }

    @Test
    void testDeleteEmployeeByNameFailure() {
        String name = "John Doe";
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", Boolean.FALSE);
        ResponseEntity<Map<String, Object>> response = ResponseEntity.ok(responseBody);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(), eq(Map.class)))
                .thenReturn((ResponseEntity) response);
        ApiClientException ex =
                assertThrows(ApiClientException.class, () -> employeeApiClient.deleteEmployeeByName(name));
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    void testDeleteEmployeeByNameClientError() {
        String name = "John Doe";
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(), eq(Map.class)))
                .thenThrow(HttpClientErrorException.create(
                        "429 TOO_MANY_REQUESTS",
                        org.springframework.http.HttpStatusCode.valueOf(429),
                        "Too Many Requests",
                        null,
                        null,
                        null));
        ApiClientException ex =
                assertThrows(ApiClientException.class, () -> employeeApiClient.deleteEmployeeByName(name));
        assertEquals(429, ex.getStatusCode());
    }
}
