package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.school.Faculty;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FacultyService {
    private final HashMap<Long, Faculty> facultyHashMap = new HashMap<>();

    private final AtomicLong idCounter = new AtomicLong();

    public Faculty createFaculty(Faculty faculty) {
        long id = idCounter.incrementAndGet();
        faculty.setId(id);
        facultyHashMap.put(id, faculty);
        return faculty;
    }

    public Faculty findFacultyById(Long id) {
        return facultyHashMap.get(id);
    }

    public Faculty updateFaculty(Faculty faculty) {
        if (!facultyHashMap.containsKey(faculty.getId())) {
            return null;
        }
        return facultyHashMap.replace(faculty.getId(), faculty) != null ? faculty : null;
    }

    public Faculty deleteFacultyById(Long id) {
        return facultyHashMap.remove(id);
    }

    public Collection<Faculty> findAllFaculties() {
        return Collections.unmodifiableCollection(facultyHashMap.values());
    }
}
