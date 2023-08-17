package com.bootpractice.board.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("존재하지 않는 사용자입니다.");
    }
}
