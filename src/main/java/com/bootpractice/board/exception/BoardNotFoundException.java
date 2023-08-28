package com.bootpractice.board.exception;

public class BoardNotFoundException extends RuntimeException {
    public BoardNotFoundException() {
            super("board does not exists");
        }
    }

