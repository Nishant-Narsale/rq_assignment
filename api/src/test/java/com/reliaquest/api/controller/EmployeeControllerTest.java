package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.model.CreateEmployeeDTO;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

class EmployeeControllerTest {
    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllEmployees_success() {
        List<Employee> employees = Arrays.asList(new Employee(), new Employee());
        when(employeeService.getAllEmployees()).thenReturn(employees);
        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employees, response.getBody());
    }

    @Test
    void getAllEmployees_error() {
        when(employeeService.getAllEmployees())
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));
        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getEmployeeById_success() {
        Employee employee = new Employee();
        when(employeeService.getEmployeeById("1")).thenReturn(employee);
        ResponseEntity<Employee> response = employeeController.getEmployeeById("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employee, response.getBody());
    }

    @Test
    void getEmployeeById_notFound() {
        when(employeeService.getEmployeeById("1"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));
        ResponseEntity<Employee> response = employeeController.getEmployeeById("1");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getEmployeesByNameSearch_success() {
        List<Employee> employees = Collections.singletonList(new Employee());
        when(employeeService.getEmployeesByNameSearch("John")).thenReturn(employees);
        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch("John");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employees, response.getBody());
    }

    @Test
    void getEmployeesByNameSearch_error() {
        when(employeeService.getEmployeesByNameSearch("John"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));
        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch("John");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getHighestSalaryOfEmployees_success() {
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(10000);
        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10000, response.getBody());
    }

    @Test
    void getHighestSalaryOfEmployees_error() {
        when(employeeService.getHighestSalaryOfEmployees())
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));
        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_success() {
        List<String> names = Arrays.asList("John", "Jane");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(names);
        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(names, response.getBody());
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_error() {
        when(employeeService.getTopTenHighestEarningEmployeeNames())
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));
        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createEmployee_success() {
        CreateEmployeeDTO dto = new CreateEmployeeDTO();
        Employee employee = new Employee();
        when(employeeService.createEmployee(dto)).thenReturn(employee);
        ResponseEntity<Employee> response = employeeController.createEmployee(dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employee, response.getBody());
    }

    @Test
    void createEmployee_error() {
        CreateEmployeeDTO dto = new CreateEmployeeDTO();
        when(employeeService.createEmployee(dto))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request"));
        ResponseEntity<Employee> response = employeeController.createEmployee(dto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteEmployeeById_success() {
        when(employeeService.deleteEmployeeById("1")).thenReturn("John");
        ResponseEntity<String> response = employeeController.deleteEmployeeById("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John", response.getBody());
    }

    @Test
    void deleteEmployeeById_notFound() {
        when(employeeService.deleteEmployeeById("1"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));
        ResponseEntity<String> response = employeeController.deleteEmployeeById("1");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteEmployeeById_tooManyRequests() {
        when(employeeService.deleteEmployeeById("1"))
                .thenThrow(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit"));
        ResponseEntity<String> response = employeeController.deleteEmployeeById("1");
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNull(response.getBody());
    }
}
