package com.reliaquest.api.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.client.ApiClientException;
import com.reliaquest.api.client.EmployeeApiClient;
import com.reliaquest.api.model.CreateEmployeeDTO;
import com.reliaquest.api.model.Employee;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeApiClient employeeApiClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET / -> 200 OK with employees JSON")
    void getAllEmployees_returnsOkAndList() throws Exception {
        Employee emp = Employee.builder()
                .id(UUID.randomUUID())
                .name("Alice")
                .salary(50000)
                .age(30)
                .title("Developer")
                .email("alice@example.com")
                .build();
        given(employeeApiClient.getAllEmployees()).willReturn(List.of(emp));

        mockMvc.perform(get("/employee"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].employee_name", is("Alice")))
                .andExpect(jsonPath("$[0].employee_salary", is(50000)))
                .andExpect(jsonPath("$[0].employee_age", is(30)))
                .andExpect(jsonPath("$[0].employee_title", is("Developer")))
                .andExpect(jsonPath("$[0].employee_email", is("alice@example.com")));
    }

    @Test
    @DisplayName("GET /employee when downstream fails -> 500 Internal Server Error")
    void getAllEmployees_whenClientFails_returns500() throws Exception {
        given(employeeApiClient.getAllEmployees())
                .willThrow(new ApiClientException("downstream error", new RuntimeException("boom"), 500));

        mockMvc.perform(get("/employee")).andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /employee -> 200 Ok")
    void createEmployee_returns200AndBody() throws Exception {
        CreateEmployeeDTO dto = CreateEmployeeDTO.builder()
                .name("Bob")
                .salary(60000)
                .age(28)
                .title("QA Engineer")
                .build();

        Employee created = Employee.builder()
                .id(UUID.randomUUID())
                .name(dto.getName())
                .salary(dto.getSalary())
                .age(dto.getAge())
                .title(dto.getTitle())
                .email("bob@example.com")
                .build();

        given(employeeApiClient.createEmployee(any())).willReturn(created);

        String payload = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.employee_name", is("Bob")))
                .andExpect(jsonPath("$.employee_salary", is(60000)))
                .andExpect(jsonPath("$.employee_age", is(28)))
                .andExpect(jsonPath("$.employee_title", is("QA Engineer")))
                .andExpect(jsonPath("$.employee_email", is("bob@example.com")));
    }

    @Test
    @DisplayName("POST /employee when downstream fails -> 500 INTERNAL SERVER ERROR")
    void createEmployee_whenClientFails_returns500() throws Exception {
        given(employeeApiClient.createEmployee(any()))
                .willThrow(new ApiClientException("downstream error", new RuntimeException("boom"), 500));

        CreateEmployeeDTO dto = CreateEmployeeDTO.builder()
                .name("Bob")
                .salary(60000)
                .age(28)
                .title("QA Engineer")
                .build();

        String payload = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isInternalServerError());
    }
}
