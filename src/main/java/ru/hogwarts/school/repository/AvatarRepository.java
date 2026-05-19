package ru.hogwarts.school.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.school.Avatar;

import java.util.Optional;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {


    Optional<Avatar> findByStudentId(Long studentId);

    @NonNull
    Page<Avatar> findAll(@NonNull Pageable pageable);


}
