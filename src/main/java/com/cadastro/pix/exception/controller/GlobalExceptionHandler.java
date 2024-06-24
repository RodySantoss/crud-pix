package com.cadastro.pix.exception.controller;

import com.cadastro.pix.controller.UserController;
import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RespDTO> handleEntityNotFound(EntityNotFoundException ex) {
        logger.error("Entity not found error: {}", ex.getMessage());
        RespDTO respDTO = new RespDTO(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespDTO> handleIllegalArgument(IllegalArgumentException ex) {
        logger.error("Illegal argument error: {}", ex.getMessage());
        RespDTO respDTO = new RespDTO(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage()
        );
        return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        FieldError error = (FieldError) ex.getBindingResult().getAllErrors().get(0);
        logger.error("Validation error: {}", error.getDefaultMessage());
        RespDTO respDTO = new RespDTO(
                HttpStatus.UNPROCESSABLE_ENTITY,
                error.getDefaultMessage()
        );
        return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<RespDTO> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        logger.error("No handler found error: {}", ex.getMessage());
        RespDTO respDTO = new RespDTO(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.error("Message not readable error: {}", ex.getMessage());
        RespDTO respDTO = new RespDTO(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        return new ResponseEntity<>(respDTO, HttpStatus.BAD_REQUEST);
    }
}
