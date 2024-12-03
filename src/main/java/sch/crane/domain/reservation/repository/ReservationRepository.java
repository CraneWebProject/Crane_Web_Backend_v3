package sch.crane.domain.reservation.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sch.crane.domain.reservation.entity.Reservation;
import sch.crane.domain.reservation.entity.enums.Instrument;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByTime(LocalDateTime time);


    @Query("SELECT r FROM Reservation  r WHERE r.user.email = :email")
    List<Reservation> findByUserEmail(@Param("email") String email);

    @Query("SELECT r FROM Reservation  r WHERE r.time >= :startOfDay AND r.time < :endOfDay AND r.instrument = :instrument")
    List<Reservation> findAllByResDateAndInstrument(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("instrument") Instrument instrument
    );

    @Query("SELECT r FROM Reservation r WHERE r.time BETWEEN :startOfDay AND :endOfDay")
    List<Reservation> findAllByDate(@Param("startOfDay") LocalDateTime startOfDay,
                                    @Param("endOfDay") LocalDateTime endOfDay);
}
