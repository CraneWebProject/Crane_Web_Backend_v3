package crane.reservationservice.service;

import crane.reservationservice.client.UserClient;
import crane.reservationservice.entity.Instrument;
import crane.reservationservice.entity.Reservation;
import crane.reservationservice.kafka.ReservationEventProducer;
import crane.reservationservice.repository.InstrumentRepository;
import crane.reservationservice.repository.ReservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

// 장비 5개 × 하루 32슬롯 = 160건 → 청크 100 + 60.
// 중간 한 건이 실패해도 앞 청크는 커밋되고, 같은 날짜를 다시 실행해도 중복 슬롯이 생기지 않는지 확인한다.
@DataJpaTest(properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
@Import(ReservationService.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED) // 테스트 기본 롤백이 청크 커밋을 가리지 않도록
class ReservationChunkTest {

    @Autowired
    ReservationService reservationService;
    @Autowired
    InstrumentRepository instrumentRepository;
    @SpyBean
    ReservationRepository reservationRepository;
    @MockBean
    UserClient userClient;
    @MockBean
    ReservationEventProducer reservationEventProducer;

    @BeforeEach
    void setUp() {
        List.of("합주", "기타", "베이스", "드럼", "키보드")
                .forEach(name -> instrumentRepository.save(Instrument.builder().name(name).isActive(true).build()));
    }

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAll();
        instrumentRepository.deleteAll();
    }

    @Test
    void keepsCommittedChunksOnFailureAndSkipsExistingSlotsOnRerun() {
        // 리포지토리는 인터페이스 프록시라 callRealMethod() 대신 스파이의 기본 위임 응답으로 실제 저장을 수행한다.
        Answer<?> realCall = Mockito.mockingDetails(reservationRepository).getMockCreationSettings().getDefaultAnswer();
        AtomicInteger saves = new AtomicInteger();
        doAnswer(invocation -> {
            if (saves.incrementAndGet() == 150) {
                throw new IllegalStateException("150번째 저장 실패");
            }
            return realCall.answer(invocation);
        }).when(reservationRepository).save(any(Reservation.class));

        assertThrows(IllegalStateException.class, () -> reservationService.createReservationAfterNDays(8));
        assertThat(reservationRepository.count()).isEqualTo(100); // 첫 청크만 커밋, 둘째 청크는 롤백

        Mockito.reset(reservationRepository);
        reservationService.createReservationAfterNDays(8);
        assertThat(reservationRepository.count()).isEqualTo(160); // 남은 슬롯만 채움

        reservationService.createReservationAfterNDays(8);
        assertThat(reservationRepository.count()).isEqualTo(160); // 재실행해도 중복 없음
    }
}
