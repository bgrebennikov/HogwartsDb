package ru.hogwarts.school.service;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.model.dto.request.StudentRequest;
import ru.hogwarts.school.model.school.Faculty;
import ru.hogwarts.school.model.school.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    @Transactional
    public Student createStudent(StudentRequest request) {
        logger.info("was invoked method for create Student");

        if (request.id() != null) {
            studentRepository.findById(request.id()).ifPresent(student -> {
                logger.error("Unable to create student. Student with id {} already exists", request.id());
                throw new IllegalArgumentException("Student already exists");
            });
        }

        Student student = new Student();
        student.setName(request.name());
        student.setAge(request.age());

        if (request.faculty() != null) {
            Faculty faculty = facultyRepository.findById(request.faculty())
                    .orElseThrow(() -> {
                        logger.warn("Cannot assign faculty to student. Faculty with id {} does not exist", request.faculty());
                        return new NoSuchElementException("Faculty not found");
                    });
            student.setFaculty(faculty);
        }

        logger.debug("Saving new student entity to the database: {}", student.getName());
        return studentRepository.save(student);
    }

    public Collection<String> getStudentsNames(String startWith) {
        logger.info("was invoked method for getting sorted student names starting with A");

        List<Student> students = studentRepository.findAll();

        return students.stream()
                .parallel()
                .filter(s -> s.getName() != null && (s.getName().toLowerCase().startsWith(startWith.toLowerCase())))
                .map(s -> s.getName().toUpperCase())
                .sorted()
                .collect(Collectors.toList());
    }

    public Student findStudentById(Long id) {
        logger.info("was invoked method for find Student by id: {}", id);
        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Student with id {} was not found", id);
                    return new NoSuchElementException("Student with id %s not found".formatted(id));
                });
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        logger.info("was invoked method for get faculty by student id: {}", studentId);
        return studentRepository.findById(studentId)
                .map(Student::getFaculty)
                .orElseThrow(() -> {
                    logger.warn("Cannot find student with id {} to fetch faculty", studentId);
                    return new NoSuchElementException("Student with id %s not found".formatted(studentId));
                });
    }

    @Transactional
    public Student updateStudent(StudentRequest request) {
        logger.info("was invoked method for update Student");
        Student existingStudent = studentRepository.findById(request.id())
                .orElseThrow(() -> {
                    logger.warn("Cannot update student. Student with id {} does not exist", request.id());
                    return new NoSuchElementException("Student with id %s not found".formatted(request.id()));
                });

        existingStudent.setName(request.name());
        existingStudent.setAge(request.age());

        if (request.faculty() != null) {
            Faculty faculty = facultyRepository.findById(request.faculty())
                    .orElseThrow(() -> {
                        logger.warn("Cannot update faculty of student. Faculty with id {} does not exist", request.faculty());
                        return new NoSuchElementException("Faculty with id %s not found".formatted(request.faculty()));
                    });
            existingStudent.setFaculty(faculty);
        } else {
            logger.debug("Removing faculty from student with id {}", request.id());
            existingStudent.setFaculty(null);
        }

        return studentRepository.save(existingStudent);
    }

    @Transactional
    public void deleteStudentById(Long id) {
        logger.info("was invoked method for delete Student by id: {}", id);
        if (!studentRepository.existsById(id)) {
            logger.warn("Attempted to delete non-existing student with id {}", id);
        }
        studentRepository.deleteById(id);
    }

    public Collection<Student> findAllStudents(int minAge, int maxAge) {

        if (minAge < 0) {
            minAge = 0;
        }
        if (maxAge < minAge) {
            logger.warn("Attempted to fetch all students from a negative maximum age");
            throw new IllegalArgumentException("Max age cannot be less than Min age");
        }

        logger.info("was invoked method for find all students with age between {} and {}", minAge, maxAge);
        return studentRepository.findAllByAgeBetween(minAge, maxAge);
    }

    public Integer getStudentsCount() {
        logger.info("was invoked method for count of students");
        return studentRepository.getStudentsCount();
    }

    public Integer getStudentAvgAge() {
        logger.info("was invoked method for avg age of students");
        return studentRepository.getAvgStudentAge();
    }

    public Collection<Student> findAllStudents(@Nonnull Integer limit) {
        logger.info("was invoked method for find all students with limit {}", limit);

        if (limit <= 0) {
            logger.warn("Received suspicious limit value: {}. Returning empty or un-limited results depends on repository", limit);
            throw new IllegalArgumentException("limit must be greater than 0");
        }

        return studentRepository.findAllStudentsWithLimit(limit);
    }
}