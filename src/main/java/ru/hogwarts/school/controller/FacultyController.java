package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.school.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    public Collection<Faculty> getFaculties() {
        return facultyService.findAllFaculties();
    }

    @GetMapping("/{facultyId}")
    public Faculty getFacultyById(
            @PathVariable Long facultyId
    ) {
        return facultyService.findFacultyById(facultyId);
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @PatchMapping
    public Faculty updateFaculty(@RequestBody Faculty faculty) {
        return facultyService.updateFaculty(faculty);
    }

    @DeleteMapping("/{facultyId}")
    public Faculty deleteFaculty(@PathVariable Long facultyId) {
        return facultyService.deleteFacultyById(facultyId);
    }


}
