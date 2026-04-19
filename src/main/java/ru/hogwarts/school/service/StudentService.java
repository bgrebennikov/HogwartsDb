package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StudentService {
    private final HashMap<Long, Student> studentHashMap = new HashMap<>();

    private final AtomicLong idCounter = new AtomicLong();

    public Student createStudent(Student student) {
        long id = idCounter.incrementAndGet();
        student.setId(id);
        studentHashMap.put(id, student);
        return student;
    }

    public Student findStudentById(Long id) {
        return studentHashMap.get(id);
    }

    public Student updateStudent(Student student) {
        if (!studentHashMap.containsKey(student.getId())) {
            return null;
        }
        return studentHashMap.replace(student.getId(), student) != null ? student : null;
    }

    public Student deleteStudentById(Long id) {
        return studentHashMap.remove(id);
    }

    public Collection<Student> findAllStudents() {
        return Collections.unmodifiableCollection(studentHashMap.values());
    }

}
