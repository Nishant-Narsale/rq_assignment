package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.client.ApiClientException;
import com.reliaquest.api.client.EmployeeApiClient;
import com.reliaquest.api.model.Employee;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

class EmployeeServiceTest {
    @Mock
    private EmployeeApiClient employeeApiClient;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeService(employeeApiClient);
    }

    @Test
    void testGetAllEmployeesSuccess() {
        Employee emp = Employee.builder().name("John").build();
        when(employeeApiClient.getAllEmployees()).thenReturn(Arrays.asList(emp));
        List<Employee> result = employeeService.getAllEmployees();
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }

    @Test
    void testGetAllEmployeesApiClientException() {
        when(employeeApiClient.getAllEmployees()).thenThrow(new ApiClientException("error", null, 429));
        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> employeeService.getAllEmployees());
        assertEquals(429, ex.getStatusCode().value());
    }

    @Test
    void testGetAllEmployeesGeneralException() {
        when(employeeApiClient.getAllEmployees()).thenThrow(new RuntimeException("fail"));
        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> employeeService.getAllEmployees());
        assertEquals(500, ex.getStatusCode().value());
    }

    @Test
    void testGetEmployeesByNameSearchSuccess() {
        Employee emp1 = Employee.builder().name("Alice").build();
        Employee emp2 = Employee.builder().name("Bob").build();
        when(employeeApiClient.getAllEmployees()).thenReturn(Arrays.asList(emp1, emp2));
        List<Employee> result = employeeService.getEmployeesByNameSearch("bob");
        assertEquals(1, result.size());
        assertEquals("Bob", result.get(0).getName());
    }

    @Test
    void testGetEmployeesByNameSearchApiClientException() {
        when(employeeApiClient.getAllEmployees()).thenThrow(new ApiClientException("error", null, 429));
        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> employeeService.getEmployeesByNameSearch("bob"));
        assertEquals(429, ex.getStatusCode().value());
    }

    @Test
    void testGetEmployeeByIdSuccess() {
        Employee emp = Employee.builder().name("Jane").build();
        when(employeeApiClient.getEmployeeById(anyString())).thenReturn(emp);
        Employee result = employeeService.getEmployeeById("id");
        assertEquals("Jane", result.getName());
    }

    @Test
    void testGetEmployeeByIdApiClientException() {
        when(employeeApiClient.getEmployeeById(anyString())).thenThrow(new ApiClientException("error", null, 404));
        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> employeeService.getEmployeeById("id"));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testGetEmployeeByIdGeneralException() {
        when(employeeApiClient.getEmployeeById(anyString())).thenThrow(new RuntimeException("fail"));
        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> employeeService.getEmployeeById("id"));
        assertEquals(500, ex.getStatusCode().value());
    }

    @Test
    void testGetHighestSalaryOfEmployeesSuccess() {
        Employee emp1 = Employee.builder().salary(1000).build();
        Employee emp2 = Employee.builder().salary(2000).build();
        when(employeeApiClient.getAllEmployees()).thenReturn(Arrays.asList(emp1, emp2));
        int result = employeeService.getHighestSalaryOfEmployees();
        assertEquals(2000, result);
    }

    @Test
    void testGetHighestSalaryOfEmployeesApiClientException() {
        when(employeeApiClient.getAllEmployees()).thenThrow(new ApiClientException("error", null, 429));
        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> employeeService.getHighestSalaryOfEmployees());
        assertEquals(429, ex.getStatusCode().value());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNamesSuccess() {
        Employee emp1 = Employee.builder().name("A").salary(100).build();
        Employee emp2 = Employee.builder().name("B").salary(200).build();
        when(employeeApiClient.getAllEmployees()).thenReturn(Arrays.asList(emp1, emp2));
        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();
        assertEquals(Arrays.asList("B", "A"), result);
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNamesApiClientException() {
        when(employeeApiClient.getAllEmployees()).thenThrow(new ApiClientException("error", null, 429));
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class, () -> employeeService.getTopTenHighestEarningEmployeeNames());
        assertEquals(429, ex.getStatusCode().value());
    }

    @Test
    void testCreateEmployeeSuccess() {
        com.reliaquest.api.model.CreateEmployeeDTO dto = new com.reliaquest.api.model.CreateEmployeeDTO();
        Employee emp = Employee.builder().name("New").build();
        when(employeeApiClient.createEmployee(any())).thenReturn(emp);
        Employee result = employeeService.createEmployee(dto);
        assertEquals("New", result.getName());
    }

    @Test
    void testCreateEmployeeApiClientException() {
        com.reliaquest.api.model.CreateEmployeeDTO dto = new com.reliaquest.api.model.CreateEmployeeDTO();
        when(employeeApiClient.createEmployee(any())).thenThrow(new ApiClientException("error", null, 429));
        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> employeeService.createEmployee(dto));
        assertEquals(429, ex.getStatusCode().value());
    }

    @Test
    void testDeleteEmployeeByIdSuccess() {
        Employee emp = Employee.builder().name("Del").build();
        when(employeeApiClient.getEmployeeById(anyString())).thenReturn(emp);
        when(employeeApiClient.deleteEmployeeByName(anyString())).thenReturn("Del");
        String result = employeeService.deleteEmployeeById("id");
        assertEquals("Del", result);
    }

    @Test
    void testDeleteEmployeeByIdNotFound() {
        when(employeeApiClient.getEmployeeById(anyString())).thenReturn(null);
        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> employeeService.deleteEmployeeById("id"));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testDeleteEmployeeByIdApiClientException() {
        when(employeeApiClient.getEmployeeById(anyString())).thenThrow(new ApiClientException("error", null, 429));
        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> employeeService.deleteEmployeeById("id"));
        assertEquals(429, ex.getStatusCode().value());
    }
}
