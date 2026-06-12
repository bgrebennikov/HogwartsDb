package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.dto.request.FacultyRequest;
import ru.hogwarts.school.model.dto.request.FacultyUpdateRequest;
import ru.hogwarts.school.model.school.Faculty;
import ru.hogwarts.school.model.school.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
@Tag(name = "Faculties", description = "Эндпоинты для управления факультетами школы")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    @Operation(
            summary = "Получить список факультетов",
            description = "Возвращает все факультеты, отфильтрованные по конкретному цвету."
    )
    public Collection<Faculty> getFaculties(
            @RequestParam(required = false) @Parameter(description = "Цвет факультета для поиска") String color,
            @RequestParam(required = false) @Parameter(description = "Имя факультета для поиска") String name
    ) {
        return facultyService.findAllFaculties(color, name);
    }

    @GetMapping("/{facultyId}")
    @Operation(
            summary = "Найти факультет по ID",
            description = "Возвращает данные факультета по его уникальному идентификатору."
    )
    @ApiResponse(responseCode = "200", description = "Факультет найден")
    @ApiResponse(responseCode = "404", description = "Факультет с таким ID не найден")
    public Faculty getFacultyById(
            @PathVariable @Parameter(description = "ID факультета") Long facultyId
    ) {
        return facultyService.findFacultyById(facultyId);
    }

    @GetMapping("/longest-name")
    public String getLongestName(){
        return facultyService.getLongestFacultyName();
    }

    @GetMapping("/{facultyId}/students")
    @Operation(
            summary = "Получить студентов факультета",
            description = "Возвращает список студентов факультета"
    )
    public Collection<Student> getStudents(
            @PathVariable Long facultyId
    ) {
        return facultyService.getStudentsByFacultyId(facultyId);
    }

    @PostMapping
    @Operation(
            summary = "Добавить новый факультет",
            description = "Создает новый факультет в системе. ID генерируется автоматически."
    )
    public Faculty createFaculty(@RequestBody FacultyRequest faculty) {
        return facultyService.createFaculty(faculty);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Обновить данные факультета",
            description = "Изменяет параметры существующего факультета."
    )
    public Faculty updateFaculty(
            @PathVariable Long id,
            @Valid @RequestBody FacultyUpdateRequest faculty) {
        return facultyService.updateFaculty(id, faculty);
    }

    @DeleteMapping("/{facultyId}")
    @Operation(
            summary = "Удалить факультет",
            description = "Удаляет факультет из базы данных по ID и возвращает объект удаленного факультета."
    )
    public void deleteFaculty(@PathVariable @Parameter(description = "ID факультета для удаления") Long facultyId) {
        facultyService.deleteFacultyById(facultyId);
    }
}