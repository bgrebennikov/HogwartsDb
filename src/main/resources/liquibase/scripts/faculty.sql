-- liquibase formatted sql
-- changeset bgrebennikov:1

create index faculty_name_color_index on faculty (name, color);