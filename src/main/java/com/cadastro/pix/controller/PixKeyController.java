package com.cadastro.pix.controller;

import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.dto.pixKey.CreatePixKeyDTO;
import com.cadastro.pix.service.PixKeyServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/pix")
@Validated
public class PixKeyController {

    private static final Logger logger = LoggerFactory.getLogger(PixKeyController.class);

    @Autowired
    private PixKeyServiceImpl pixKeyService;

    @PostMapping
    public ResponseEntity<RespDTO> createPixKey(@Valid @RequestBody CreatePixKeyDTO pixKeyDTO) {
        logger.info("Request to create PIX key received: {}", pixKeyDTO);
        RespDTO respDTO = pixKeyService.createPixKey(pixKeyDTO);
        logger.info("PIX key created successfully: {}", respDTO);
        return ResponseEntity.status(HttpStatus.OK).body(respDTO);
    }

    @GetMapping
    public ResponseEntity<RespDTO> findAllPixKeys() {
        logger.info("Request to find all PIX keys received");
        RespDTO respDTO = pixKeyService.findAllPixKeys();
        logger.info("PIX keys retrieved successfully: {}", respDTO);
        return ResponseEntity.status(HttpStatus.OK).body(respDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespDTO> findPixKeyById(@PathVariable UUID id) {
        logger.info("Request to find PIX key by id received: {}", id);
        RespDTO respDTO = pixKeyService.findPixKeyById(id);
        logger.info("PIX key retrieved successfully for id {}: {}", id, respDTO);
        return ResponseEntity.status(HttpStatus.OK).body(respDTO);
    }

    @GetMapping("/by-type")
    public ResponseEntity<RespDTO> findPixKeyByType(@RequestParam("keyType") String keyType) {
        logger.info("Request to find PIX keys by type received: {}", keyType);
        RespDTO user = pixKeyService.findPixKeysByType(keyType);
        logger.info("PIX keys retrieved successfully by type {}: {}", keyType, user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/by-agency-and-account")
    public ResponseEntity<RespDTO> findPixKeyByAgencyAndAccount(
            @RequestParam("agencyNumber") Integer agencyNumber,
            @RequestParam("accountNumber") Integer accountNumber) {
        logger.info("Request to find PIX keys by agency number {} and account number {} received", agencyNumber, accountNumber);
        RespDTO user = pixKeyService.findPixKeysByAgencyAndAccount(agencyNumber, accountNumber);
        logger.info("PIX keys retrieved successfully by agency number {} and account number {}: {}", agencyNumber, accountNumber, user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/by-user-name")
    public ResponseEntity<RespDTO> findPixKeyByUserName(@RequestParam("userName") String userName) {
        logger.info("Request to find PIX keys by username received: {}", userName);
        RespDTO user = pixKeyService.findPixKeysByUserName(userName);
        logger.info("PIX keys retrieved successfully by username {}: {}", userName, user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/by-created")
    public ResponseEntity<RespDTO> findPixKeyByCreatedAt(@RequestParam("createdAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAt) {
        logger.info("Request to find PIX keys by creation date received: {}", createdAt);
        RespDTO user = pixKeyService.findPixKeysByCreatedAt(createdAt);
        logger.info("PIX keys retrieved successfully by creation date {}: {}", createdAt, user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/by-inactivated")
    public ResponseEntity<RespDTO> findPixKeyByInactivatedAt(@RequestParam("inactivatedAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inactivatedAt) {
        logger.info("Request to find PIX keys by inactivation date received: {}", inactivatedAt);
        RespDTO user = pixKeyService.findPixKeysByInactivatedAt(inactivatedAt);
        logger.info("PIX keys retrieved successfully by inactivation date {}: {}", inactivatedAt, user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RespDTO> deletePixKey(@PathVariable UUID id) {
        logger.info("Request to delete PIX key received for id {}", id);
        RespDTO respDTO = pixKeyService.deletePixKey(id);
        logger.info("PIX key deleted successfully for id {}: {}", id, respDTO);
        return ResponseEntity.status(HttpStatus.OK).body(respDTO);
    }
}