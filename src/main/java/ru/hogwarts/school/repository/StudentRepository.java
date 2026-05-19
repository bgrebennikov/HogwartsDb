package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.school.Student;

import java.util.Collection;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Collection<Student> findAllByAgeBetween(int min, int max);

    @Query(value = "SELECT count (*) from Student", nativeQuery = true)
    Integer getStudentsCount();

    @Query(
            value = "SELECT avg (age) from Student",
            nativeQuery = true
    )
    Integer getAvgStudentAge();

    @Query(
            value = "select * from Student order by id desc limit :limit",
            nativeQuery = true
    )
    Collection<Student> findAllStudentsWithLimit(@Param("limit") int limit);

}
