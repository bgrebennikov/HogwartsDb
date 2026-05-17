package ru.hogwarts.school;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import ru.hogwarts.school.model.dto.request.FacultyRequest;
import ru.hogwarts.school.model.dto.request.FacultyUpdateRequest;
import ru.hogwarts.school.model.school.Faculty;
import ru.hogwarts.school.model.school.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public class FacultyControllerTest {


    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private FacultyRepository facultyRepository;

    @MockitoBean
    private StudentRepository studentRepository;

    @Test
    void testCreateFaculty() {
        FacultyRequest request = new FacultyRequest("Gryffindor", "Red");

        Faculty savedFaculty = new Faculty();
        savedFaculty.setId(1L);
        savedFaculty.setName("Gryffindor");
        savedFaculty.setColor("Red");

        when(facultyRepository.save(any(Faculty.class))).thenReturn(savedFaculty);

        restTestClient.post()
                .uri("/faculty")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Faculty.class)
                .value(faculty -> {
                    Assertions.assertNotNull(faculty);
                    Assertions.assertEquals(1L, faculty.getId());
                    Assertions.assertEquals("Gryffindor", faculty.getName());
                    Assertions.assertEquals("Red", faculty.getColor());
                });
    }

    @Test
    void testGetFacultyById() {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Gryffindor");
        faculty.setColor("Red");

        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        restTestClient.get()
                .uri("/faculty/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Faculty.class)
                .value(actual -> {
                    Assertions.assertNotNull(actual);
                    Assertions.assertEquals(1L, actual.getId());
                    Assertions.assertEquals("Gryffindor", actual.getName());
                    Assertions.assertEquals("Red", actual.getColor());
                });
    }

    @Test
    void testGetStudentsByFacultyId() {

        Student student = new Student();
        student.setName("Harry");

        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setStudents(List.of(student));

        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        restTestClient.get()
                .uri("/faculty/1/students")
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .value(actual -> {
                    Assertions.assertNotNull(actual);
                    Assertions.assertEquals(1L, actual.size());
                });
    }

    @Test
    void testUpdateFaculty() {
        FacultyUpdateRequest updateRequest = new FacultyUpdateRequest("RavenClaw", "Blue");

        Faculty existingFaculty = new Faculty();
        existingFaculty.setId(1L);

        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setId(1L);
        updatedFaculty.setName("RavenClaw");
        updatedFaculty.setColor("Blue");

        when(facultyRepository.findById(1L)).thenReturn(Optional.of(existingFaculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(updatedFaculty);

        restTestClient.patch()
                .uri("/faculty/1")
                .body(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Faculty.class)
                .value(actual -> {
                    Assertions.assertNotNull(actual);
                    Assertions.assertEquals(updateRequest.name(), actual.getName());
                    Assertions.assertEquals(updateRequest.color(), actual.getColor());
                });
    }

    @Test
    void testDeleteFaculty() {
        doNothing().when(facultyRepository).deleteById(1L);

        restTestClient.delete()
                .uri("/faculty/1")
                .exchange()
                .expectStatus().isOk();

        verify(facultyRepository, times(1)).deleteById(1L);
    }


    @Test
    void testFindAllFacultiesByColor() {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setColor("Gryffindor");
        faculty.setName("Red");

        when(facultyRepository.findAllByColorIgnoreCaseOrNameIgnoreCase(anyString(), isNull())).thenReturn(List.of(faculty));

        restTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/faculty")
                                .queryParam("color", "green")
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(
                        new ParameterizedTypeReference<List<Faculty>>() {
                        }
                )
                .value(actual -> {
                    Assertions.assertNotNull(actual);
                    Assertions.assertEquals(1, actual.size());

                    Assertions.assertEquals(faculty.getId(), actual.get(0).getId());
                    Assertions.assertEquals(faculty.getName(), actual.get(0).getName());
                    Assertions.assertEquals(faculty.getColor(), actual.get(0).getColor());
                });

    }

}
