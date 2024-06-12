package com.cadastro.pix.controller;

import com.cadastro.pix.domain.PixKey;
import com.cadastro.pix.domain.ReqObj;
import com.cadastro.pix.service.PixKeyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pix")
@Validated
public class PixKeyController {

    @Autowired
    private PixKeyService pixKeyService;

    @PostMapping
    public ResponseEntity<?> createPixKey(@Valid @RequestBody ReqObj reqObj) {
        try {
            PixKey createdPixKey = pixKeyService.createPixKey(reqObj);
            return ResponseEntity.status(HttpStatus.OK).body(createdPixKey.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }

//    @GetMapping
//    public ResponseEntity<List<PixKey>> getAllPixKeys() {
//        List<PixKey> pixKeys = pixKeyService.getAllPixKeys();
//        return ResponseEntity.ok(pixKeys);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getPixKeyById(@PathVariable UUID id) {
//        return pixKeyService.getPixKeyById(id);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updatePixKey(@PathVariable UUID id, @Valid @RequestBody PixKey pixKey) {
//        try {
//            ResponseEntity<?> updatedPixKey = pixKeyService.updatePixKey(id, pixKey);
//            return ResponseEntity.ok(updatedPixKey);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deletePixKey(@PathVariable UUID id) {
//        try {
//            return pixKeyService.deletePixKey(id);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
//        }
//    }
}