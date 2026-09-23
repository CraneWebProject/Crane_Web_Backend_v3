package crane.reservationservice.service;

import crane.reservationservice.client.UserClient;
import crane.reservationservice.entity.Instrument;
import crane.reservationservice.entity.Reservation;
import crane.reservationservice.entity.enums.Status;
import crane.reservationservice.kafka.ReservationEventProducer;
import crane.reservationservice.repository.InstrumentRepository;
import crane.reservationservice.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// 삭제 배치(batch-service deleteReservationJob → DELETE /api/v1/reservations/expired)가 호출하는 로직.
// 지난 주(8일 전~7일 전) 구간에서 예약자가 없는 건만 지우고, 예약된 건과 구간 밖은 남겨야 한다.
@DataJpaTest(properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
@Import(ReservationService.class)
class ReservationDeleteTest {

    @Autowired
    ReservationService reservationService;
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    InstrumentRepository instrumentRepository;
    @MockBean
    UserClient userClient;
    @MockBean
    ReservationEventProducer reservationEventProducer;

    Instrument instrument;

    @BeforeEach
    void setUp() {
        instrument = instrumentRepository.save(Instrument.builder().name("합주").isActive(true).build());
    }

    @Test
    void deletesOnlyUnclaimedReservationsOfLastWeek() {
        LocalDateTime now = LocalDateTime.now();
        save(now.minusDays(8).withHour(10), null);  // 삭제 대상
        save(now.minusDays(8).withHour(11), 1L);    // 예약자 있음 → 유지
        save(now.minusDays(5).withHour(10), null);  // 구간 밖 → 유지

        reservationService.deleteReservation();

        List<Reservation> left = reservationRepository.findAll();
        assertThat(left).hasSize(2);
        assertThat(left).noneMatch(r -> r.getUserId() == null && r.getTime().isBefore(now.minusDays(7)));
    }

    private void save(LocalDateTime time, Long userId) {
        reservationRepository.save(Reservation.builder()
                .userId(userId)
                .status(Status.PENDING)
                .possible(userId == null)
                .instrument(instrument)
                .time(time)
                .build());
    }
}
