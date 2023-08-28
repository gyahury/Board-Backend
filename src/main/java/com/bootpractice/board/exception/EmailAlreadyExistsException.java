package com.bootpractice.board.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("email already exists");
    }
}
