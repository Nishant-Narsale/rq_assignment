package com.reliaquest.api.service;

import com.reliaquest.api.client.ApiClientException;
import com.reliaquest.api.client.EmployeeApiClient;
import com.reliaquest.api.model.CreateEmployeeDTO;
import com.reliaquest.api.model.Employee;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeApiClient employeeApiClient;

    public EmployeeService(EmployeeApiClient employeeApiClient) {
        this.employeeApiClient = employeeApiClient;
    }

    public List<Employee> getAllEmployees() {
        logger.debug("[START] getAllEmployees");
        long start = System.currentTimeMillis();
        try {
            List<Employee> employees = employeeApiClient.getAllEmployees();
            logger.debug("[END] getAllEmployees - duration: {}ms", System.currentTimeMillis() - start);
            return employees;
        } catch (ApiClientException e) {
            logger.error("[ERROR] getAllEmployees - ApiClientException", e);
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatusCode()), e.getMessage(), e);
        } catch (Exception e) {
            logger.error("[ERROR] getAllEmployees - Exception", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching employees", e);
        }
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        logger.debug("[START] getEmployeesByNameSearch - searchString: {}", searchString);
        long start = System.currentTimeMillis();
        try {
            List<Employee> employees = employeeApiClient.getAllEmployees();
            List<Employee> result = employees.stream()
                    .filter(e ->
                            e.getName() != null && e.getName().toLowerCase().contains(searchString.toLowerCase()))
                    .collect(Collectors.toList());
            logger.debug(
                    "[END] getEmployeesByNameSearch - found {} employees, duration: {}ms",
                    result.size(),
                    System.currentTimeMillis() - start);
            return result;
        } catch (ApiClientException e) {
            logger.error("[ERROR] getEmployeesByNameSearch - ApiClientException", e);
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatusCode()), e.getMessage(), e);
        } catch (Exception e) {
            logger.error("[ERROR] getEmployeesByNameSearch - Exception", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error searching employees", e);
        }
    }

    public Employee getEmployeeById(String id) {
        logger.debug("[START] getEmployeeById - id: {}", id);
        long start = System.currentTimeMillis();
        try {
            Employee employee = employeeApiClient.getEmployeeById(id);
            logger.debug(
                    "[END] getEmployeeById - found: {}, duration: {}ms",
                    employee != null ? employee.getName() : "null",
                    System.currentTimeMillis() - start);
            return employee;
        } catch (ApiClientException e) {
            logger.error("[ERROR] getEmployeeById - ApiClientException", e);
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatusCode()), e.getMessage(), e);
        } catch (Exception e) {
            logger.error("[ERROR] getEmployeeById - Exception", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching employee", e);
        }
    }

    public int getHighestSalaryOfEmployees() {
        logger.debug("[START] getHighestSalaryOfEmployees");
        long start = System.currentTimeMillis();
        try {
            List<Employee> employees = employeeApiClient.getAllEmployees();
            int maxSalary =
                    employees.stream().mapToInt(Employee::getSalary).max().orElse(0);
            logger.debug(
                    "[END] getHighestSalaryOfEmployees - maxSalary: {}, duration: {}ms",
                    maxSalary,
                    System.currentTimeMillis() - start);
            return maxSalary;
        } catch (ApiClientException e) {
            logger.error("[ERROR] getHighestSalaryOfEmployees - ApiClientException", e);
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatusCode()), e.getMessage(), e);
        } catch (Exception e) {
            logger.error("[ERROR] getHighestSalaryOfEmployees - Exception", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching salary", e);
        }
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        logger.debug("[START] getTopTenHighestEarningEmployeeNames");
        long start = System.currentTimeMillis();
        try {
            List<Employee> employees = employeeApiClient.getAllEmployees();
            List<String> names = employees.stream()
                    .sorted((a, b) -> Integer.compare(b.getSalary(), a.getSalary()))
                    .limit(10)
                    .map(Employee::getName)
                    .collect(Collectors.toList());
            logger.debug(
                    "[END] getTopTenHighestEarningEmployeeNames - names: {}, duration: {}ms",
                    names,
                    System.currentTimeMillis() - start);
            return names;
        } catch (ApiClientException e) {
            logger.error("[ERROR] getTopTenHighestEarningEmployeeNames - ApiClientException", e);
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatusCode()), e.getMessage(), e);
        } catch (Exception e) {
            logger.error("[ERROR] getTopTenHighestEarningEmployeeNames - Exception", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching employee names", e);
        }
    }

    public Employee createEmployee(CreateEmployeeDTO employeeInput) {
        logger.debug("[START] createEmployee - input: {}", employeeInput);
        long start = System.currentTimeMillis();
        try {
            Employee employee = employeeApiClient.createEmployee(employeeInput);
            logger.debug(
                    "[END] createEmployee - created: {}, duration: {}ms",
                    employee != null ? employee.getName() : "null",
                    System.currentTimeMillis() - start);
            return employee;
        } catch (ApiClientException e) {
            logger.error("[ERROR] createEmployee - ApiClientException", e);
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatusCode()), e.getMessage(), e);
        } catch (Exception e) {
            logger.error("[ERROR] createEmployee - Exception", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating employee", e);
        }
    }

    public String deleteEmployeeById(String id) {
        logger.debug("[START] deleteEmployeeById - id: {}", id);
        long start = System.currentTimeMillis();
        try {
            Employee employee = getEmployeeById(id);
            if (employee == null || employee.getName() == null) {
                logger.error("No employee found for id: {}", id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
            }
            String deletedName = employeeApiClient.deleteEmployeeByName(employee.getName());
            logger.debug(
                    "[END] deleteEmployeeById - deleted: {}, duration: {}ms",
                    deletedName,
                    System.currentTimeMillis() - start);
            return deletedName;
        } catch (ApiClientException e) {
            logger.error("[ERROR] deleteEmployeeById - ApiClientException", e);
            if (e.getStatusCode() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            } else if (e.getStatusCode() == 429) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, e.getMessage(), e);
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
            }
        } catch (ResponseStatusException e) {
            logger.error("[ERROR] deleteEmployeeById - ResponseStatusException", e);
            throw e;
        } catch (Exception e) {
            logger.error("[ERROR] deleteEmployeeById - Exception", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting employee", e);
        }
    }
}
