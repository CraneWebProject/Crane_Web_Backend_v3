package sch.crane.domain.reply.exception;

import sch.crane.domain.common.exception.NotFoundException;

public class ReplyNotExistException extends NotFoundException {
    public ReplyNotExistException(String message) {
        super("message");
    }
}
