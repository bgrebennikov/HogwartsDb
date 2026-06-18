package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.dto.request.FacultyRequest;
import ru.hogwarts.school.model.dto.request.FacultyUpdateRequest;
import ru.hogwarts.school.model.school.Faculty;
import ru.hogwarts.school.model.school.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(FacultyRequest faculty) {
        logger.info("was invoked method for create Faculty");

        logger.debug("Mapping FacultyRequest to entity for name: {}", faculty.name());
        Faculty entity = asEntity(faculty);

        return facultyRepository.save(entity);
    }

    public Faculty findFacultyById(Long id) {
        logger.info("was invoked method for find Faculty by id: {}", id);
        return facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Faculty with id {} was not found", id);
                    return new NoSuchElementException("Faculty with id %s not found".formatted(id));
                });
    }

    public String getLongestFacultyName() {
        logger.info("was invoked method for find Faculty by name");

        return facultyRepository.findAll().stream()
                .parallel()
                .map(Faculty::getName)
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(String::length))
                .orElse("Not faculties found");
    }

    public Collection<Student> getStudentsByFacultyId(Long facultyId) {
        logger.info("was invoked method for get students by faculty id: {}", facultyId);
        return facultyRepository.findById(facultyId)
                .map(Faculty::getStudents)
                .orElseThrow(() -> {
                    logger.warn("Cannot fetch students. Faculty with id {} does not exist", facultyId);
                    return new NoSuchElementException("Faculty with id %s not found".formatted(facultyId));
                });
    }

    public Faculty updateFaculty(Long id, FacultyUpdateRequest request) {
        logger.info("was invoked method for update Faculty with id: {}", id);
        return facultyRepository.findById(id)
                .map(f -> {
                    logger.debug("Updating fields for faculty ID {}. New name: {}, new color: {}", id, request.name(), request.color());
                    f.setName(request.name());
                    f.setColor(request.color());
                    return facultyRepository.save(f);
                })
                .orElseThrow(() -> {
                    logger.warn("Cannot update faculty. Faculty with id {} does not exist", id);
                    return new NoSuchElementException("Not found faculty with ID: %s".formatted(id));
                });
    }

    public void deleteFacultyById(Long id) {
        logger.info("was invoked method for delete Faculty by id: {}", id);
        if (!facultyRepository.existsById(id)) {
            logger.warn("Attempted to delete non-existing faculty with id {}", id);
        }
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> findAllFaculties(String name, String color) {
        logger.info("was invoked method for find all faculties");

        boolean isNameBlank = (name == null || name.isBlank());
        boolean isColorBlank = (color == null || color.isBlank());

        if (isNameBlank && isColorBlank) {
            logger.debug("Both name and color filters are empty. Fetching all faculties from database");
            return facultyRepository.findAll();
        }

        logger.debug("Filtering faculties by name: '{}' OR color: '{}'", name, color);
        return facultyRepository.findAllByColorIgnoreCaseOrNameIgnoreCase(name, color);
    }

    private Faculty asEntity(FacultyRequest dto) {
        return new Faculty(
                dto.name(),
                dto.color()
        );
    }
}