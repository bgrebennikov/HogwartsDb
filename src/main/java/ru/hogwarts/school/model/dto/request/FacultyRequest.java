package ru.hogwarts.school.model.dto.request;


import jakarta.validation.constraints.NotBlank;

public record FacultyRequest(
        @NotBlank(message = "Name is mandatory") String name,
        @NotBlank(message = "Color is mandatory") String color
) {
}
