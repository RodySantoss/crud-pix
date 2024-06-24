package com.cadastro.pix.controller;

import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.dto.pixKey.CreatePixKeyDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.service.PixKeyService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/pix")
@Validated
public class PixKeyController {

    private static final Logger log = LoggerFactory.getLogger(PixKeyController.class);

    @Autowired
    private PixKeyService pixKeyService;

    @PostMapping
    public ResponseEntity<RespDTO> createPixKey(@Valid @RequestBody CreatePixKeyDTO pixKeyDTO) {
        log.info("Request to create PIX key received: {}", pixKeyDTO);
        try {
            RespDTO respDTO = pixKeyService.createPixKey(pixKeyDTO);
            log.info("PIX key created successfully: {}", respDTO);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while creating PIX key: {}", e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while creating PIX key: {}", e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping
    public ResponseEntity<RespDTO> findAll() {
        log.info("Request to find all PIX keys received");
        try {
            RespDTO respDTO = pixKeyService.findAll();
            log.info("PIX keys retrieved successfully: {}", respDTO);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while finding all PIX keys: {}", e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while finding all PIX keys: {}", e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespDTO> findById(@PathVariable UUID id) {
        log.info("Request to find PIX key by id received: {}", id);
        try {
            RespDTO respDTO = pixKeyService.findById(id);
            log.info("PIX key retrieved successfully for id {}: {}", id, respDTO);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while finding PIX key by id {}: {}", id, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while finding PIX key by id {}: {}", id, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/by-type")
    public ResponseEntity<RespDTO> findByType(@RequestParam("keyType") String keyType) {
        log.info("Request to find PIX keys by type received: {}", keyType);
        try {
            RespDTO user = pixKeyService.findByType(keyType);
            log.info("PIX keys retrieved successfully by type {}: {}", keyType, user);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while finding PIX keys by type {}: {}", keyType, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while finding PIX keys by type {}: {}", keyType, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/by-agency-and-account")
    public ResponseEntity<RespDTO> findByAgencyAndAccount(
            @RequestParam("agencyNumber") Integer agencyNumber,
            @RequestParam("accountNumber") Integer accountNumber) {
        log.info("Request to find PIX keys by agency number {} and account number {} received", agencyNumber, accountNumber);
        try {
            RespDTO user = pixKeyService.findByAgencyAndAccount(agencyNumber, accountNumber);
            log.info("PIX keys retrieved successfully by agency number {} and account number {}: {}", agencyNumber, accountNumber, user);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while finding PIX keys by agency number {} and account number {}: {}", agencyNumber, accountNumber, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while finding PIX keys by agency number {} and account number {}: {}", agencyNumber, accountNumber, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/by-user-name")
    public ResponseEntity<RespDTO> findByUserName(@RequestParam("userName") String userName) {
        log.info("Request to find PIX keys by username received: {}", userName);
        try {
            RespDTO user = pixKeyService.findByUserName(userName);
            log.info("PIX keys retrieved successfully by username {}: {}", userName, user);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while finding PIX keys by username {}: {}", userName, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while finding PIX keys by username {}: {}", userName, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/by-created")
    public ResponseEntity<RespDTO> findByCreatedAt(@RequestParam("createdAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAt) {
        log.info("Request to find PIX keys by creation date received: {}", createdAt);
        try {
            RespDTO user = pixKeyService.findByCreatedAt(createdAt);
            log.info("PIX keys retrieved successfully by creation date {}: {}", createdAt, user);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while finding PIX keys by creation date {}: {}", createdAt, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while finding PIX keys by creation date {}: {}", createdAt, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/by-inactivated")
    public ResponseEntity<RespDTO> findByInactivatedAt(@RequestParam("inactivatedAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inactivatedAt) {
        log.info("Request to find PIX keys by inactivation date received: {}", inactivatedAt);
        try {
            RespDTO user = pixKeyService.findByInactivatedAt(inactivatedAt);
            log.info("PIX keys retrieved successfully by inactivation date {}: {}", inactivatedAt, user);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while finding PIX keys by inactivation date {}: {}", inactivatedAt, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while finding PIX keys by inactivation date {}: {}", inactivatedAt, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RespDTO> deletePixKey(@PathVariable UUID id) {
        log.info("Request to delete PIX key received for id {}", id);
        try {
            RespDTO respDTO = pixKeyService.deletePixKey(id);
            log.info("PIX key deleted successfully for id {}: {}", id, respDTO);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException while deleting PIX key for id {}: {}", id, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException while deleting PIX key for id {}: {}", id, e.getMessage());
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        FieldError error = (FieldError) ex.getBindingResult().getAllErrors().get(0);
        log.error("Validation error: {}", error.getDefaultMessage());
        RespDTO respDTO = new RespDTO(
                HttpStatus.UNPROCESSABLE_ENTITY,
                error.getDefaultMessage()
        );
        return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}