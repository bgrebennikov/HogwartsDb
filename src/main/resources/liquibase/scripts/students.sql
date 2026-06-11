-- liquibase formatted sql
-- changeset bgrebennikov:1

create index student_name_index on student(name);