package sch.crane.domain.reservation.exception;

import sch.crane.domain.common.exception.NotFoundException;

public class ReservationNotFoundException extends NotFoundException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
