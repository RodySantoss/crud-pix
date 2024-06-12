package com.cadastro.pix.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String mensagem) {
        super(mensagem);
    }
}