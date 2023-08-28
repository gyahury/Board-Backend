package com.bootpractice.board.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("user does not exist");
    }
}
