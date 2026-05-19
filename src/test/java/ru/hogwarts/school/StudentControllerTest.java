package ru.hogwarts.school;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import ru.hogwarts.school.model.dto.request.StudentRequest;
import ru.hogwarts.school.model.school.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private FacultyRepository facultyRepository;


    @Test
    void testPostStudent() {
        StudentRequest request = new StudentRequest(
                null, "Harry", 22, null
        );

        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setName("Harry");
        savedStudent.setAge(22);

        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        restTestClient.post()
                .uri("/student")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Student.class)
                .value(student -> {
                    Assertions.assertNotNull(student);
                    Assertions.assertNotNull(student.getId());
                    Assertions.assertEquals("Harry", student.getName());
                    Assertions.assertEquals(22, student.getAge());
                });
    }

    @Test
    void testGetStudent() {
        Student student = new Student();
        student.setId(123L);
        student.setName("Hermione");
        student.setAge(20);

        when(studentRepository.findById(123L)).thenReturn(Optional.of(student));

        restTestClient.get()
                .uri("/student/{studentId}", 123L)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Student.class)
                .value(
                        actual -> {
                            Assertions.assertNotNull(actual);
                            Assertions.assertEquals(student.getId(), actual.getId());
                            Assertions.assertEquals(student.getName(), actual.getName());
                            Assertions.assertEquals(student.getAge(), actual.getAge());
                        }
                );

    }

    @Test
    void testUpdateStudent() {
        StudentRequest request = new StudentRequest(
                1L, "New Name", 20, null
        );

        Student existingStudent = new Student();
        existingStudent.setId(1L);
        existingStudent.setName("Old Name");
        existingStudent.setAge(20);

        Student updatedStudent = new Student();
        updatedStudent.setId(1L);
        updatedStudent.setName(request.name());
        updatedStudent.setAge(20);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);


        restTestClient.patch()
                .uri("/student")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Student.class)
                .value(actual -> {
                    Assertions.assertNotNull(actual);
                    Assertions.assertEquals(request.name(), actual.getName());
                });
    }

    @Test
    void testDeleteStudent() {
        doNothing().when(studentRepository).deleteById(1L);

        restTestClient.delete()
                .uri("/student/{studentId}", 1L)
                .exchange()
                .expectStatus().isOk();

        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetStudentsCount() {

        when(studentRepository.getStudentsCount()).thenReturn(5);


        restTestClient.get()
                .uri("/student/count")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .value(actual -> {
                    Assertions.assertNotNull(actual);
                    Assertions.assertEquals(5, actual);
                });


    }

    @Test
    void testGetAvgStudentsAge() {

        when(studentRepository.getAvgStudentAge()).thenReturn(20);

        restTestClient.get()
                .uri("/student/avg/age")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .value(actual -> {
                    Assertions.assertNotNull(actual);
                    Assertions.assertEquals(20, actual);
                });
    }

    @Test
    void testGetLastNStudents() {

        Collection<Student> students = LongStream.range(0, 5)
                .mapToObj(index -> new Student(index, "Student_%s".formatted(index), 20 + (int) index))
                .toList();


        when(studentRepository.findAllStudentsWithLimit(5)).thenReturn(
                students
        );

        restTestClient.get()
                .uri(builder -> builder
                        .path("/student/last").queryParam("limit", 5).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Student>>() {

                })
                .value(actual -> {
                    Assertions.assertNotNull(actual);
                    Assertions.assertEquals(5, actual.size());
                });
    }

}
