package com.bootpractice.board.exception;

public class UserMismatchException extends RuntimeException {
    public UserMismatchException() {
        super("author is mismatched");
    }
}
