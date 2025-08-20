package com.reliaquest.api.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CreateEmployeeDTOTest {
    @Test
    void testCreateEmployeeDTOGettersAndSetters() {
        CreateEmployeeDTO dto = new CreateEmployeeDTO();
        dto.setName("John Doe");
        dto.setSalary(50000);
        dto.setAge(30);
        dto.setTitle("Developer");

        assertEquals("John Doe", dto.getName());
        assertEquals(50000, dto.getSalary());
        assertEquals(30, dto.getAge());
        assertEquals("Developer", dto.getTitle());
    }

    @Test
    void testCreateEmployeeDTONoArgsConstructor() {
        CreateEmployeeDTO dto = new CreateEmployeeDTO();
        assertNotNull(dto);
    }

    @Test
    void testCreateEmployeeDTOToString() {
        CreateEmployeeDTO dto = new CreateEmployeeDTO();
        dto.setName("Alice");
        dto.setSalary(70000);
        dto.setAge(35);
        dto.setTitle("Manager");
        String str = dto.toString();
        assertTrue(str.contains("Alice"));
        assertTrue(str.contains("70000"));
        assertTrue(str.contains("35"));
        assertTrue(str.contains("Manager"));
    }

    @Test
    void testCreateEmployeeDTOEqualsAndHashCode() {
        CreateEmployeeDTO dto1 = new CreateEmployeeDTO();
        dto1.setName("Bob");
        dto1.setSalary(80000);
        dto1.setAge(40);
        dto1.setTitle("Lead");
        CreateEmployeeDTO dto2 = new CreateEmployeeDTO();
        dto2.setName("Bob");
        dto2.setSalary(80000);
        dto2.setAge(40);
        dto2.setTitle("Lead");
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
