package com.reliaquest.api.controller;

import com.reliaquest.api.model.CreateEmployeeDTO;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/employee")
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeDTO> {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.info("[REQUEST] getAllEmployees");
        long start = System.currentTimeMillis();
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            logger.info(
                    "[RESPONSE] getAllEmployees - {} employees, duration: {}ms",
                    employees.size(),
                    System.currentTimeMillis() - start);
            return ResponseEntity.ok(employees);
        } catch (ResponseStatusException e) {
            logger.error("[ERROR] getAllEmployees - {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            logger.error("[ERROR] getAllEmployees - Exception: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        logger.info("[REQUEST] getEmployeesByNameSearch - searchString: {}", searchString);
        long start = System.currentTimeMillis();
        try {
            List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
            logger.info(
                    "[RESPONSE] getEmployeesByNameSearch - {} employees, duration: {}ms",
                    employees.size(),
                    System.currentTimeMillis() - start);
            return ResponseEntity.ok(employees);
        } catch (ResponseStatusException e) {
            logger.error("[ERROR] getEmployeesByNameSearch - {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            logger.error("[ERROR] getEmployeesByNameSearch - Exception: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        logger.info("[REQUEST] getEmployeeById - id: {}", id);
        long start = System.currentTimeMillis();
        try {
            Employee employee = employeeService.getEmployeeById(id);
            logger.info(
                    "[RESPONSE] getEmployeeById - found: {}, duration: {}ms",
                    employee != null ? employee.getName() : "null",
                    System.currentTimeMillis() - start);
            return ResponseEntity.ok(employee);
        } catch (ResponseStatusException e) {
            logger.error("[ERROR] getEmployeeById - {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            logger.error("[ERROR] getEmployeeById - Exception: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        logger.info("[REQUEST] getHighestSalaryOfEmployees");
        long start = System.currentTimeMillis();
        try {
            int maxSalary = employeeService.getHighestSalaryOfEmployees();
            logger.info(
                    "[RESPONSE] getHighestSalaryOfEmployees - maxSalary: {}, duration: {}ms",
                    maxSalary,
                    System.currentTimeMillis() - start);
            return ResponseEntity.ok(maxSalary);
        } catch (ResponseStatusException e) {
            logger.error("[ERROR] getHighestSalaryOfEmployees - {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            logger.error("[ERROR] getHighestSalaryOfEmployees - Exception: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        logger.info("[REQUEST] getTopTenHighestEarningEmployeeNames");
        long start = System.currentTimeMillis();
        try {
            List<String> names = employeeService.getTopTenHighestEarningEmployeeNames();
            logger.info(
                    "[RESPONSE] getTopTenHighestEarningEmployeeNames - names: {}, duration: {}ms",
                    names,
                    System.currentTimeMillis() - start);
            return ResponseEntity.ok(names);
        } catch (ResponseStatusException e) {
            logger.error("[ERROR] getTopTenHighestEarningEmployeeNames - {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            logger.error("[ERROR] getTopTenHighestEarningEmployeeNames - Exception: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<Employee> createEmployee(CreateEmployeeDTO employeeInput) {
        logger.info("[REQUEST] createEmployee - input: {}", employeeInput);
        long start = System.currentTimeMillis();
        try {
            Employee employee = employeeService.createEmployee(employeeInput);
            logger.info(
                    "[RESPONSE] createEmployee - created: {}, duration: {}ms",
                    employee != null ? employee.getName() : "null",
                    System.currentTimeMillis() - start);
            return ResponseEntity.ok(employee);
        } catch (ResponseStatusException e) {
            logger.error("[ERROR] createEmployee - {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            logger.error("[ERROR] createEmployee - Exception: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        logger.info("[REQUEST] deleteEmployeeById - id: {}", id);
        long start = System.currentTimeMillis();
        try {
            String deletedName = employeeService.deleteEmployeeById(id);
            logger.info(
                    "[RESPONSE] deleteEmployeeById - deleted: {}, duration: {}ms",
                    deletedName,
                    System.currentTimeMillis() - start);
            return ResponseEntity.ok(deletedName);
        } catch (ResponseStatusException e) {
            logger.error("[ERROR] deleteEmployeeById - {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            logger.error("[ERROR] deleteEmployeeById - Exception: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
