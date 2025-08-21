package com.reliaquest.api.boot;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class StartupLogger implements CommandLineRunner {

    @Autowired
    private EmployeeService employeeService;

    @Override
    public void run(String... args) {
        // Logging employees at Startup as instructed in README
        List<Employee> employees = employeeService.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println(
                    "\n************************************ No employees found at Startup ***********************\n");
        } else {
            System.out.println(
                    "\n************************************ Employees at Startup ********************************\n");
            for (Employee employee : employees) {
                System.out.println(employee);
            }
            System.out.println(
                    "\n*****************************************************************************************\n");
        }
    }
}
