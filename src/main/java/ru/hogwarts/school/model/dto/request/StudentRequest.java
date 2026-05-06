package ru.hogwarts.school.model.dto.request;

public record StudentRequest (
    Long id,
    String name,
    int age,
    Long faculty
){}
