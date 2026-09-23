package crane.reservationservice.service;

import crane.reservationservice.client.UserClient;
import crane.reservationservice.common.exceptions.BadRequestException;
import crane.reservationservice.dto.ReservationRequestDto;
import crane.reservationservice.dto.ReservationResponseDto;
import crane.reservationservice.dto.UserResponseDto;
import crane.reservationservice.entity.Instrument;
import crane.reservationservice.entity.Reservation;
import crane.reservationservice.entity.enums.Status;
import crane.reservationservice.entity.enums.UserRole;
import crane.reservationservice.kafka.ReservationEventProducer;
import crane.reservationservice.repository.InstrumentRepository;
import crane.reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    // 예약 슬롯 일괄 생성 시 한 트랜잭션에 저장할 건수
    private static final int CHUNK_SIZE = 100;

    private final ReservationRepository reservationRepository;
    private final InstrumentRepository instrumentRepository;
    private final UserClient userClient;
    private final TransactionTemplate transactionTemplate;

    private final ReservationEventProducer reservationEventProducer;

    //Batch Scheduler 를 통해 미리 1주간 예약을 생성해 둠.
    //예약 신청시 생성된 예약에 user, team 등 정보를 추가하고 예약 가능 상태를 불가능으로 바꿈.
    //합주 신청시 해당 시간 장비 사용 가능을 불가능으로 바꿈.
    //밤 11시에 배치로 다음주 예약 생성
    //밤 12시에 다음주 합주 신청 open
    //낮 12시에 다음주 장비 신청 open

    //초기 예약 생성
    //서버 실행 시 오늘부터 1주일간의 예약을 열린 상태로 생성함.
    //이미 있는 슬롯은 건너뛰므로 여러 번 실행해도 중복 생성되지 않음.
    @Transactional(propagation = Propagation.NOT_SUPPORTED) // 청크마다 별도 트랜잭션을 쓰기 위해 클래스의 readOnly 트랜잭션에 합류하지 않음
    public void initReservation(){
        for(int i = 0; i < 8; i++){
            createSlots(i, true);
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createReservationAfterNDays(int n){
        createSlots(n, false);
    }

    // 제로 오프셋: 슬롯 시각을 대상 날짜 00:00:00.000 기준으로 계산해, 몇 번 실행해도 같은 슬롯은 같은 (장비, 시각) 키를 갖게 한다.
    // No-Offset 페이징처럼 실행 시각·순번이 아니라 키 값으로 슬롯을 식별하므로, 재실행 시 이미 있는 슬롯을 건너뛸 수 있다.
    // CHUNK_SIZE 건마다 별도 트랜잭션으로 저장해, 한 청크가 실패해도 앞선 청크는 커밋된 상태로 남는다.
    // ponytail: 슬롯마다 exists 조회 1회. 규모가 커지면 청크 단위 일괄 조회로 바꿀 것.
    // ponytail: 부분 실패 후 재실행으로 뒤늦게 생성된 열린 슬롯은 같은 시각의 기존 합주/장비 예약에 의한 차단을 반영하지 않음.
    private void createSlots(int n, boolean possible){
        LocalDateTime reservationDate = LocalDate.now().plusDays(n).atStartOfDay();
        List<Instrument> instruments = instrumentRepository.findAll();

        //24시간동안 30분 단위로 예약 생성
        List<Reservation> slots = new ArrayList<>();
        for(int hour = 8; hour < 24; hour++){
            for(int minute = 0; minute < 60; minute += 30){
                LocalDateTime time = reservationDate.plusHours(hour).plusMinutes(minute);

                for(Instrument instrument : instruments){
                    slots.add(Reservation.builder()
                            .userId(null)
                            .status(Status.PENDING)
                            .possible(possible)
                            .instrument(instrument)
                            .time(time)
                            .build());
                }
            }
        }

        for(int from = 0; from < slots.size(); from += CHUNK_SIZE){
            List<Reservation> chunk = slots.subList(from, Math.min(from + CHUNK_SIZE, slots.size()));
            try {
                transactionTemplate.executeWithoutResult(status -> chunk.stream()
                        .filter(r -> !reservationRepository.existsByInstrumentAndTime(r.getInstrument(), r.getTime()))
                        .forEach(reservationRepository::save));
            } catch (RuntimeException e) {
                // 이 청크만 롤백됨. 같은 날짜로 다시 실행하면 남은 슬롯만 생성된다.
                log.error("예약 슬롯 청크 저장 실패 - date: {}, chunk: {}~{}",
                        reservationDate.toLocalDate(), from, from + chunk.size() - 1, e);
                throw e;
            }
        }
    }

    //n일 뒤 합주 예약 open
    @Transactional
    public void openEnsembleAfterNDays(int n){
        LocalDateTime startOfDay = LocalDateTime.now().plusDays(n).withHour(0).withMinute(0);
        LocalDateTime endOfDay = LocalDateTime.now().plusDays(n).withHour(23).withMinute(59);

        List<Reservation> reservationList = reservationRepository.findAllByDate(startOfDay, endOfDay);

        reservationList.stream()
                .filter(r -> r.getInstrument().getName().equals("합주"))
                .forEach(r -> r.updateReservation(r.getTime(), true, r.getInstrument(), r.getUserId(), r.getStatus()));
    }

    //n일 뒤 장비 예약 open
    @Transactional
    public void openInstAfterNDays(int n){
        LocalDateTime startOfDay = LocalDateTime.now().plusDays(n).withHour(0).withMinute(0);
        LocalDateTime endOfDay = LocalDateTime.now().plusDays(n).withHour(23).withMinute(59);

        List<Reservation> reservationList = reservationRepository.findAllByDate(startOfDay, endOfDay);

        reservationList.stream()
                .filter(r -> !r.getInstrument().getName().equals("합주"))
                .forEach(r -> r.updateReservation(r.getTime(), true, r.getInstrument(), r.getUserId(), r.getStatus()));
    }

    //단일 예약 생성
    //관리자 및 임원만 가능
    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto reservationRequestDto, Long userId) {
        UserResponseDto user = userClient.getUserById(userId).getData();
        Instrument instrument = instrumentRepository.findByIdOrElseThrow(reservationRequestDto.getInstrumentId());

        //권한 체크 추가

        Reservation reservation = Reservation.builder()
                .instrument(instrument)
                .possible(true)
                .time(reservationRequestDto.getTime())
                .status(Status.PENDING)
                .userId(null)
                .build();
        reservationRepository.save(reservation);
        return ReservationResponseDto.from(reservation);
    }

    //예약 확인
    public ReservationResponseDto getReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdOrElseThrow(reservationId);
        return ReservationResponseDto.from(reservation);
    }

    //예약 수정
    @Transactional
    public ReservationResponseDto updateReservation(ReservationRequestDto reservationRequestDto, Long userId) {
        UserResponseDto user = userClient.getUserById(userId).getData();
        Reservation reservation = reservationRepository.findByIdOrElseThrow(reservationRequestDto.getReservationId());
        Instrument instrument = instrumentRepository.findByIdOrElseThrow(reservationRequestDto.getInstrumentId());

        if(!reservation.getUserId().equals(userId)
                && !(Objects.equals(user.getUserRole(), UserRole.ADMIN.toString()) || Objects.equals(user.getUserRole(), UserRole.MANAGER.toString()))) {
            throw new BadRequestException("권한이 없는 사용자입니다");
        }

        //값이 null인 경우 기존 값 유지
        LocalDateTime updateTime = reservationRequestDto.getTime() != null
                ? reservationRequestDto.getTime()
                : reservation.getTime();
        Instrument updateInstrument = reservationRequestDto.getInstrumentId() != null
                ? instrument
                : reservation.getInstrument();

        reservation.updateReservation(
                updateTime,
                reservation.getPossible(),
                updateInstrument,
                reservation.getUserId(),
                reservation.getStatus()
        );

        return ReservationResponseDto.from(reservation);
    }

    //예약하기
    @Transactional
    public ReservationResponseDto makeReservation(Long reservationId, Long userId) {
        UserResponseDto user = userClient.getUserById(userId).getData();
        Reservation reservation = reservationRepository.findByIdOrElseThrow(reservationId);

        if(!reservation.getPossible()) {
            throw new BadRequestException("예약 불가능한 시간대입니다.");
        }

        if(reservation.getTime().minusMinutes(30).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("지난 시간은 예약이 불가능합니다");
        }

        List<Reservation> reservationList = reservationRepository.findByTime(reservation.getTime());
        if(reservation.getInstrument().getName().equals("합주")){
            reservationList.stream()
                    .filter(r -> !r.getInstrument().getName().equals("합주"))
                    .forEach(r -> r.updateReservation(r.getTime(), false, r.getInstrument(), r.getUserId(),r.getStatus()));
        }else{
            reservationList.stream()
                    .filter(r -> r.getInstrument().getName().equals("합주"))
                    .forEach(r -> r.updateReservation(r.getTime(), false, r.getInstrument(), r.getUserId(), r.getStatus()));
        }

        reservation.updateReservation(
                reservation.getTime(),
                false,
                reservation.getInstrument(),
                userId,
                Status.CONFIRMED
        );

        //예약 완료 메시지 처리 로직
        reservationEventProducer.sendReservationSuccessEvent(reservationId, reservation.getReservationId(), reservation.getTime(), userId);

        return ReservationResponseDto.from(reservation);
    }

    //예약 취소
    //신청자, 관리자, 매니저만 취소 가능
    //지난 예약은 취소 불가
    @Transactional
    public ReservationResponseDto cancelReservation(Long reservationId, Long userId) {
        UserResponseDto user = userClient.getUserById(userId).getData();
        Reservation reservation = reservationRepository.findByIdOrElseThrow(reservationId);

        if(!reservation.getUserId().equals(userId)
                && !(Objects.equals(user.getUserRole(), UserRole.ADMIN.toString()) || Objects.equals(user.getUserRole(), UserRole.MANAGER.toString()))) {
            throw new BadRequestException("권한이 없는 사용자입니다");
        }

        if(reservation.getTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("지난 예약은 취소할 수 없습니다");
        }

        //합주 예약 취소인 경우 장비 예약 허용

        List<Reservation> reservationList = reservationRepository.findByTime(reservation.getTime());
        if(reservation.getInstrument().getName().equals("합주")){
            reservationList.stream()
                    .filter(r -> !r.getInstrument().getName().equals("합주"))
                    .forEach(r -> r.updateReservation(r.getTime(), true, r.getInstrument(), r.getUserId(), r.getStatus()));
        }else{
            reservationList.stream()
                    .filter(r -> r.getInstrument().getName().equals("합주"))
                    .forEach(r -> r.updateReservation(r.getTime(), true, r.getInstrument(), r.getUserId(), r.getStatus()));
        }

        reservation.updateReservation(
                reservation.getTime(),
                true,
                reservation.getInstrument(),
                null,
                null
        );

        //예약 취소 완료 발송 로직
        reservationEventProducer.sendReservationCancelEvent(reservationId, reservation.getReservationId(), reservation.getTime(), userId);

        return ReservationResponseDto.from(reservation);
    }

    //예약 삭제
    //batch scheduler 로 구현
    //새벽 n시에 전 주 예약자가 없는 예약 삭제
    @Transactional
    public void deleteReservation(){
        LocalDateTime startOfDay = LocalDateTime.now().minusDays(8).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfNextDay = LocalDateTime.now().minusDays(7).withHour(0).withMinute(0).withSecond(0);

        reservationRepository.deleteReservationByDateAndUserIsNull(startOfDay, startOfNextDay);
    }


    //일간 장비별 예약 목록

    public List<ReservationResponseDto> findReservationByDayAndInst(LocalDate date, Long instrumentId) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();
        Instrument instrument = instrumentRepository.findByIdOrElseThrow(instrumentId);


        List<Reservation> reservationList = reservationRepository.findAllByResDateAndInstrument(
                startOfDay,
                startOfNextDay,
                instrument.getInstrumentId()
        );

        return reservationList.stream()
                .map(ReservationResponseDto::from)
                .toList();
    }


    //사용자별 예약 목록
    //TODO: paging 처리 필요
    public List<ReservationResponseDto> findReservationByUser(Long userId) {
        UserResponseDto user = userClient.getUserById(userId).getData();

        List<Reservation> reservationList = reservationRepository.findByUserId(userId);

        return reservationList.stream()
                .map(ReservationResponseDto::from)
                .toList();
    }

}