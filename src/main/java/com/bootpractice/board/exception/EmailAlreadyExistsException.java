package com.bootpractice.board.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("이미 존재하는 이메일입니다.");
    }
}
