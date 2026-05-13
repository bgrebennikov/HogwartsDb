package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.dto.request.FacultyRequest;
import ru.hogwarts.school.model.dto.request.FacultyUpdateRequest;
import ru.hogwarts.school.model.school.Faculty;
import ru.hogwarts.school.model.school.Student;
import ru.hogwarts.school.service.FacultyService;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
class FacultyControllerMvcTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateFaculty() throws Exception {

        FacultyRequest request = new FacultyRequest("Gryffindor", "Red");

        Faculty createdFaculty = new Faculty();
        createdFaculty.setId(1L);
        createdFaculty.setName("Gryffindor");
        createdFaculty.setColor("Red");

        when(facultyService.createFaculty(any(FacultyRequest.class))).thenReturn(createdFaculty);

        mockMvc.perform(post("/faculty")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        status().isOk()
                )
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));
    }

    @Test
    void testGetFacultyById() throws Exception {
        String requestId = "1";

        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Gryffindor");
        faculty.setColor("Red");

        when(facultyService.findFacultyById(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/{facultyId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));

    }

    @Test
    void testGetStudentsByFacultyId() throws Exception {
        String requestId = "1";

        Student student = new Student();
        student.setId(1L);
        student.setName("Harry");
        student.setAge(22);

        when(facultyService.getStudentsByFacultyId(1L)).thenReturn(List.of(student));

        mockMvc.perform(get("/faculty/{facultyId}/students", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Harry"))
                .andExpect(jsonPath("$[0].age").value(22));
    }

    @Test
    void testUpdateFaculty() throws Exception {
        FacultyUpdateRequest request = new FacultyUpdateRequest(
                "Ravenclaw", "Blue"
        );

        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setId(1L);
        updatedFaculty.setName("Ravenclaw");
        updatedFaculty.setColor("Blue");

        when(facultyService.updateFaculty(eq(1L), any(FacultyUpdateRequest.class))).thenReturn(updatedFaculty);

        mockMvc.perform(patch("/faculty/1")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ravenclaw"))
                .andExpect(jsonPath("$.color").value("Blue"));
    }

    @Test
    void testGetFacultiesWithFilter() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Gryffindor");
        faculty.setColor("Red");

        when(facultyService.findAllFaculties("Red", null)).thenReturn(List.of(faculty));

        mockMvc.perform(get("/faculty").param("color", "Red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[0].color").value("Red"));
    }


    @Test
    void testDeleteFaculty() throws Exception {
        String requestId = "1";
        mockMvc.perform(delete("/faculty/{facultyId}", requestId))
                .andExpect(status().isOk());
    }


}