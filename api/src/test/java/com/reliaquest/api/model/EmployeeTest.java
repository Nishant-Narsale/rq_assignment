package com.reliaquest.api.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EmployeeTest {
    @Test
    void testEmployeeGettersAndSetters() {
        Employee emp = new Employee();
        emp.setName("John Doe");
        emp.setSalary(50000);
        emp.setAge(30);
        emp.setTitle("Developer");
        emp.setEmail("john@example.com");

        assertEquals("John Doe", emp.getName());
        assertEquals(50000, emp.getSalary());
        assertEquals(30, emp.getAge());
        assertEquals("Developer", emp.getTitle());
        assertEquals("john@example.com", emp.getEmail());
    }

    @Test
    void testEmployeeBuilder() {
        Employee emp = Employee.builder()
                .name("Jane Doe")
                .salary(60000)
                .age(28)
                .title("Architect")
                .email("jane@example.com")
                .build();
        assertEquals("Jane Doe", emp.getName());
        assertEquals(60000, emp.getSalary());
        assertEquals(28, emp.getAge());
        assertEquals("Architect", emp.getTitle());
        assertEquals("jane@example.com", emp.getEmail());
    }

    @Test
    void testEmployeeNoArgsConstructor() {
        Employee emp = new Employee();
        assertNotNull(emp);
    }

    @Test
    void testEmployeeToString() {
        Employee emp = Employee.builder()
                .name("Alice")
                .salary(70000)
                .age(35)
                .title("Manager")
                .email("alice@example.com")
                .build();
        String str = emp.toString();
        assertTrue(str.contains("Alice"));
        assertTrue(str.contains("70000"));
        assertTrue(str.contains("35"));
        assertTrue(str.contains("Manager"));
        assertTrue(str.contains("alice@example.com"));
    }

    @Test
    void testEmployeeEqualsAndHashCode() {
        Employee emp1 = Employee.builder()
                .name("Bob")
                .salary(80000)
                .age(40)
                .title("Lead")
                .email("bob@example.com")
                .build();
        Employee emp2 = Employee.builder()
                .name("Bob")
                .salary(80000)
                .age(40)
                .title("Lead")
                .email("bob@example.com")
                .build();
        assertEquals(emp1, emp2);
        assertEquals(emp1.hashCode(), emp2.hashCode());
    }
}
