package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.school.Avatar;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {




}
