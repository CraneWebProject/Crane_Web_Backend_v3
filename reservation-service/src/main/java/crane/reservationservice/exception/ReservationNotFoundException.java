package crane.reservationservice.exception;

import crane.reservationservice.common.exceptions.NotFoundException;

public class ReservationNotFoundException extends NotFoundException {

    public ReservationNotFoundException(String message) {
        super(message);
    }
}
