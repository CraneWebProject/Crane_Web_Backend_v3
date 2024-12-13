package crane.reservationservice.repository;

import crane.reservationservice.entity.Instrument;
import crane.reservationservice.entity.Reservation;
import crane.reservationservice.exception.ReservationNotFoundException;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByTime(LocalDateTime time);

    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId")
    List<Reservation> findByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM Reservation r WHERE r.time BETWEEN :startOfDay AND :endOfDay")
    List<Reservation> findAllByDate(@Param("startOfDay") LocalDateTime startOfDay,
                                    @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT r FROM Reservation r WHERE r.instrument.instrumentId = :instrumentId")
    List<Reservation> findAllByInstrument(@Param("instrumentId") Long instrumentId);


    @Query("SELECT r FROM Reservation r WHERE r.time >= :startOfDay AND r.time < :startOfNextDay AND r.instrument.instrumentId = :InstrumentId")
    List<Reservation> findAllByResDateAndInstrument(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("startOfNextDay") LocalDateTime startOfNextDay,
            @Param("instrumentId") Long InstrumentId
    );

    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.time BETWEEN :startOfDay AND :startOfNextDay AND r.userId is NULL")
    void deleteReservationByDateAndUserIsNull(@Param("startOfDay") LocalDateTime startOfDay,
                                              @Param("startOfNextDay") LocalDateTime startOfNextDay);

    default Reservation findByIdOrElseThrow(long id) {
        return findById(id).orElseThrow(() -> new ReservationNotFoundException(id + "는 없는 예약입니다."));
    }
}
