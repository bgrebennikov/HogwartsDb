package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.school.Faculty;
import ru.hogwarts.school.model.school.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.NoSuchElementException;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
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


    public Student updateStudent(Student student) {
        return studentRepository.save(student);
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
