package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.dto.request.StudentRequest;
import ru.hogwarts.school.model.school.Student;
import ru.hogwarts.school.service.StudentService;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateStudent() throws Exception {
        StudentRequest request = new StudentRequest(
                null, "Harry", 20, null
        );

        Student student = new Student();
        student.setId(1);
        student.setName("Harry");

        when(studentService.createStudent(any(StudentRequest.class)))
                .thenReturn(student);

        mockMvc.perform(
                        post("/student")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Harry"));
    }


    @Test
    void findStudentById() throws Exception {

        String studentId = "1";

        Student student = new Student();
        student.setId(1);
        student.setName("Harry");
        student.setAge(20);

        when(studentService.findStudentById(1L)).thenReturn(student);

        mockMvc.perform(
                        get("/student/{studentId}", studentId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Harry"));
    }

    @Test
    void testGetAllStudents() throws Exception {
        Student student = new Student();
        student.setId(1);
        student.setName("Harry");
        student.setAge(20);

        when(studentService.findAllStudents(anyInt(), anyInt())).thenReturn(List.of(student));

        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Harry"));
    }

    @Test
    void testUpdateStudent() throws Exception {
        StudentRequest request = new StudentRequest(1L, "Harry", 30, null);

        Student student = new Student();
        student.setId(1);
        student.setName("Harry");

        when(studentService.updateStudent(any(StudentRequest.class))).thenReturn(student);

        mockMvc.perform(patch("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Harry"));
    }

    @Test
    void testDeleteStudent() throws Exception {
        mockMvc.perform(delete("/student/{studentId}", 1L))
                .andExpect(status().isOk());
    }


}