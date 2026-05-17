package ru.hogwarts.school.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.hogwarts.school.model.dto.response.ErrorResponse;

import java.io.UncheckedIOException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UncheckedIOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(UncheckedIOException exception) {
        return ResponseEntity.internalServerError().body(
                new ErrorResponse(
                        exception.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                )
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(
                        ex.getMessage(), HttpStatus.NOT_FOUND.value()
                )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                )
        );
    }
}
