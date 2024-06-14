package com.cadastro.pix.controller;

import com.cadastro.pix.domain.pixKey.PixKey;
import com.cadastro.pix.domain.ReqObj;
import com.cadastro.pix.domain.RespDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import com.cadastro.pix.service.PixKeyService;
import jakarta.validation.Valid;
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

    @Autowired
    private PixKeyService pixKeyService;

    @PostMapping
    public ResponseEntity<RespDTO> createPixKey(@Valid @RequestBody ReqObj reqObj) {
        try {
            RespDTO respDTO = pixKeyService.createPixKey(reqObj);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping
    public ResponseEntity<RespDTO> findAll() {
        try {
            RespDTO respDTO = pixKeyService.findAll();
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespDTO> findById(@PathVariable UUID id) {
        try {
            RespDTO respDTO = pixKeyService.findById(id);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/by-type")
    public ResponseEntity<RespDTO> findByAgencyAndAccount(@RequestParam("keyType") String keyType) {
        try {
            RespDTO user = pixKeyService.findByType(keyType);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (EntityNotFoundException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
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
        try {
            RespDTO user = pixKeyService.findByAgencyAndAccount(agencyNumber, accountNumber);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (EntityNotFoundException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/by-user-name")
    public ResponseEntity<RespDTO> findByUsername(@RequestParam("createdAt") String userName) {
        try {
            RespDTO user = pixKeyService.findByUserName(userName);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (EntityNotFoundException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/by-created")
    public ResponseEntity<RespDTO> findByCreatedAt(@RequestParam("createdAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAt) {
        try {
            RespDTO user = pixKeyService.findByCreatedAt(createdAt);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (EntityNotFoundException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/by-inactivated")
    public ResponseEntity<RespDTO> findByInactivatedAt(@RequestParam("inactivatedAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inactivatedAt) {
        try {
            RespDTO user = pixKeyService.findByInactivatedAt(inactivatedAt);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (EntityNotFoundException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RespDTO> deletePixKey(@PathVariable UUID id) {
        try {
            RespDTO respDTO = pixKeyService.deletePixKey(id);
            return ResponseEntity.status(HttpStatus.OK).body(respDTO);
        } catch (EntityNotFoundException e) {
            RespDTO respDTO = new RespDTO(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
            return new ResponseEntity<>(respDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
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
        RespDTO respDTO = new RespDTO(
                HttpStatus.UNPROCESSABLE_ENTITY,
                error.getDefaultMessage()
        );
        return new ResponseEntity<>(respDTO, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}