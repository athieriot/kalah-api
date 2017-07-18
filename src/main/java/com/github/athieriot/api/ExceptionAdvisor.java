package com.github.athieriot.api;

import com.github.athieriot.exception.GameOverException;
import com.github.athieriot.exception.IllegalMoveException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.ResponseEntity.*;

@ControllerAdvice
public class ExceptionAdvisor {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleIOException(NoSuchElementException ex) {
        return notFound().build();
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<String> handleIOException(InvalidParameterException ex) {
        return badRequest().body(ex.getLocalizedMessage());
    }

    @ExceptionHandler(GameOverException.class)
    public ResponseEntity<String> handleIOException(GameOverException ex) {
        return status(CONFLICT).body(ex.getLocalizedMessage());
    }

    @ExceptionHandler(IllegalMoveException.class)
    public ResponseEntity<String> handleIOException(IllegalMoveException ex) {
        return status(FORBIDDEN).body(ex.getLocalizedMessage());
    }
}