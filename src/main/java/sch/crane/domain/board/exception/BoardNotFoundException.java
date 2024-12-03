package sch.crane.domain.board.exception;

import sch.crane.domain.common.exception.NotFoundException;

public class BoardNotFoundException extends NotFoundException {
    public BoardNotFoundException(String message) {
        super("message");
    }
}
