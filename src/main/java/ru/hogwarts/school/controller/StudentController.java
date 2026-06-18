package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.dto.request.StudentRequest;
import ru.hogwarts.school.model.school.Faculty;
import ru.hogwarts.school.model.school.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("/student")
@Tag(name = "Students", description = "Эндпоинты для управления данными студентов")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    @Operation(
            summary = "Получить всех студентов",
            description = "Возвращает список всех студентов. Можно отфильтровать по возрастному диапазону."
    )
    public Collection<Student> findAllStudents(
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Минимальный возраст для фильтрации") Integer minAge,
            @RequestParam(defaultValue = "99999999")
            @Parameter(description = "Максимальный возраст для фильтрации") Integer maxAge
    ) {
        return studentService.findAllStudents(minAge, maxAge);
    }

    @GetMapping("/names")
    @Operation(
            summary = "Получить имена студентов в алфавитном порядке"
    )
    public Collection<String> findAllStudentsByNames(
            @RequestParam(defaultValue = "А")
            @Parameter(description = "Получить имена всех студентов, чье имя начинается с определенной буквы")
            String startWith
    ) {
        return studentService.getStudentsNames(startWith);
    }

    @GetMapping("/{studentId}")
    @Operation(
            summary = "Найти студента по ID",
            description = "Возвращает подробную информацию о конкретном студенте."
    )
    @ApiResponse(responseCode = "200", description = "Студент успешно найден")
    @ApiResponse(responseCode = "404", description = "Студент с таким ID не существует")
    public Student findStudentById(@PathVariable @Parameter(description = "ID студента") Long studentId) {
        return studentService.findStudentById(studentId);
    }


    @GetMapping("/count")
    @Operation(
            summary = "Получить количество студентов",
            description = "Возвращает количество записей о всех студентах из БД."
    )
    public Integer countStudents() {
        return studentService.getStudentsCount();
    }

    @GetMapping("/avg/age")
    @Operation(
            summary = "Получить средний возраст студента",
            description = "Возвращает средний возраст студента на основе всех учащихся. Если студентов нет - вернет 0."
    )
    Integer getAverageAge() {
        return studentService.getStudentAvgAge();
    }

    @GetMapping("/avg/age/stream")
    @Operation(
            summary = "Получить средний возраст студента с использованием parallel stream",
            description = "Возвращает средний возраст студента на основе всех учащихся. Если студентов нет - вернет 0."
    )
    Integer getAverageAgeWithStream() {
        return studentService.getAvgStudentAgeWithStream();
    }

    @GetMapping("/last")
    @Operation(
            summary = "Получить последних студентов",
            description = "По умолчанию возвращает 5 последних студентов. Укажите поличество записей в параметре \"limit\" "
    )
    Collection<Student> getLastNStudents(@RequestParam(defaultValue = "5") Integer limit) {
        return studentService.findAllStudents(limit);
    }

    @GetMapping("/{studentId}/faculty")
    @Operation(
            summary = "Получить факультет студента",
            description = "Возвращает подробную информацию и факультете в котором учится студент"
    )
    public Faculty getFaculty(
            @PathVariable Long studentId
    ) {
        return studentService.getFacultyByStudentId(studentId);
    }

    @PostMapping
    @Operation(
            summary = "Создать нового студента",
            description = "Принимает объект Student в формате JSON и сохраняет его в базе данных."
    )
    public Student createStudent(@RequestBody StudentRequest student) {
        return studentService.createStudent(student);
    }

    @PatchMapping
    @Operation(
            summary = "Обновить данные студента",
            description = "Обновляет существующую информацию о студенте. Требуется передать полный объект с корректным ID."
    )
    public Student updateStudent(@RequestBody StudentRequest request) {
        return studentService.updateStudent(request);
    }

    @DeleteMapping("/{studentId}")
    @Operation(
            summary = "Удалить студента",
            description = "Удаляет запись о студенте из базы данных по его идентификатору."
    )
    public void deleteStudent(@PathVariable @Parameter(description = "ID студента для удаления") long studentId) {
        studentService.deleteStudentById(studentId);
    }

    @GetMapping("/print-parallel")
    public ResponseEntity<Void> printParallel() {
        studentService.printStudentsParallel();
        return ResponseEntity.ok().build();
    }
}