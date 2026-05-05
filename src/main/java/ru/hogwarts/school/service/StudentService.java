package ru.hogwarts.school.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.dto.request.StudentRequest;
import ru.hogwarts.school.model.school.Faculty;
import ru.hogwarts.school.model.school.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.NoSuchElementException;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    public Student createStudent(StudentRequest request) {
        Student exists = studentRepository.findById(request.id()).orElse(null);
        if (exists != null) {
            throw new EntityExistsException("Student already exists");
        }


        Student student = new Student();
        student.setName(request.name());
        student.setAge(request.age());

        if (request.faculty() != null) {
            Faculty faculty = facultyRepository.findById(request.faculty()).orElseThrow(
                    () -> new NoSuchElementException("Faculty not found")
            );
            student.setFaculty(faculty);
        }


        return studentRepository.save(student);
    }

    public Student findStudentById(Long id) {
        return studentRepository.findById(id).orElseThrow();
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        return studentRepository.findById(studentId)
                .map(Student::getFaculty)
                .orElseThrow(
                        () -> new NoSuchElementException("Student with id %s not found".formatted(studentId))
                );
    }


    public Student updateStudent(StudentRequest request) {
        Student existingStudent = studentRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("Student with id %s not found".formatted(request.id())));

        existingStudent.setName(request.name());
        existingStudent.setAge(request.age());

        if (request.faculty() != null) {
            Faculty faculty = facultyRepository.findById(request.faculty())
                    .orElseThrow(() -> new NoSuchElementException("Faculty with id %s not found".formatted(request.faculty())));
            existingStudent.setFaculty(faculty);
        } else {
            existingStudent.setFaculty(null);
        }

        return studentRepository.save(existingStudent);

    }

    public void deleteStudentById(Long id) {
        studentRepository.deleteById(id);
    }

    public Collection<Student> findAllStudents(
            int minAge, int maxAge
    ) {

        return studentRepository.findAllByAgeBetween(minAge, maxAge);
    }

}
