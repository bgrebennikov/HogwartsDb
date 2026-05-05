package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.dto.request.FacultyRequest;
import ru.hogwarts.school.model.dto.request.FacultyUpdateRequest;
import ru.hogwarts.school.model.school.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.NoSuchElementException;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(FacultyRequest faculty) {
        return facultyRepository.save(asEntity(faculty));
    }

    public Faculty findFacultyById(Long id) {
        return facultyRepository.findById(id).orElseThrow();
    }

    public Faculty updateFaculty(Long id, FacultyUpdateRequest request) {
        return facultyRepository.findById(id)
                .map(f -> {
                    f.setName(request.name());
                    f.setColor(request.color());
                    return facultyRepository.save(f);
                })
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Not found faculty with ID: %s"
                                        .formatted(id)
                        )
                );
    }

    public void deleteFacultyById(Long id) {
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> findAllFaculties(String color) {
        if (color == null) {
            return facultyRepository.findAll();
        }
        return facultyRepository.findAllByColorIgnoreCase(color);
    }

    private Faculty asEntity(FacultyRequest dto) {
        return new Faculty(
                dto.name(),
                dto.color()
        );
    }
}
