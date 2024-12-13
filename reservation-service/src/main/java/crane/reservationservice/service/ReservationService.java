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
import crane.reservationservice.repository.InstrumentRepository;
import crane.reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final InstrumentRepository instrumentRepository;
    private final UserClient userClient;

    //Batch Scheduler 를 통해 미리 1주간 예약을 생성해 둠.
    //예약 신청시 생성된 예약에 user, team 등 정보를 추가하고 예약 가능 상태를 불가능으로 바꿈.
    //합주 신청시 해당 시간 장비 사용 가능을 불가능으로 바꿈.
    //밤 11시에 배치로 다음주 예약 생성
    //밤 12시에 다음주 합주 신청 open
    //낮 12시에 다음주 장비 신청 open

    //초기 예약 생성
    //서버 실행 시 오늘부터 1주일간의 예약을 모두 생성함.
    //예약이 없는경우 실행되도록 할 것.
    @Transactional
    public void initReservation(){
        LocalDateTime startOfDay;
        LocalDateTime endOfDay;
        List<Reservation> reservationList;

        for(int i = 0; i < 8; i++){
            startOfDay = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            endOfDay = LocalDateTime.now().plusDays(i).withHour(23).withMinute(59).withSecond(59);

            reservationList = reservationRepository.findAllByDate(startOfDay, endOfDay);

            if(reservationList.isEmpty()){
                createReservationAfterNDays(i);
                openEnsembleAfterNDays(i);
                openInstAfterNDays(i);
            }
        }
    }

    @Transactional
    public void createReservationAfterNDays(int n){
        LocalDateTime reservationDate = LocalDateTime.now().plusDays(n).withHour(0).withMinute(0).withSecond(0);

        //24시간동안 30분 단위로 예약 생성
        for(int hour = 8; hour < 24; hour++){
            for(int minute = 0; minute < 60; minute += 30){
                LocalDateTime time = reservationDate.plusHours(hour).plusMinutes(minute);

                List <Instrument> instruments = instrumentRepository.findAll();

                for(Instrument instrument : instruments){
                    Reservation reservation = Reservation.builder()
                            .userId(null)
                            .status(Status.PENDING)
                            .possible(false)
                            .instrument(instrument)
                            .time(time)
                            .build();

                    reservationRepository.save(reservation);
                }
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
                .forEach(r -> r.updateReservation(r.getTime(), true, r.getInstrument(), r.getUserId()));
    }

    //n일 뒤 장비 예약 open
    @Transactional
    public void openInstAfterNDays(int n){
        LocalDateTime startOfDay = LocalDateTime.now().plusDays(n).withHour(0).withMinute(0);
        LocalDateTime endOfDay = LocalDateTime.now().plusDays(n).withHour(23).withMinute(59);

        List<Reservation> reservationList = reservationRepository.findAllByDate(startOfDay, endOfDay);

        reservationList.stream()
                .filter(r -> !r.getInstrument().getName().equals("합주"))
                .forEach(r -> r.updateReservation(r.getTime(), true, r.getInstrument(), r.getUserId()));
    }



    //단일 예약 생성
    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto reservationRequestDto, Long userId) {
        UserResponseDto user = userClient.getUserById(userId).getData();
        Instrument instrument = instrumentRepository.findByIdOrElseThrow(reservationRequestDto.getInstrumentId());


        Reservation reservation = Reservation.builder()
                .instrument(instrument)
                .possible(true)
                .time(reservationRequestDto.getTime())
                .status(Status.PENDING)
                .userId(userId)
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
                reservation.getUserId()
        );

        return ReservationResponseDto.from(reservation);
    }

    //예약하기
    @Transactional
    public ReservationResponseDto makeReservation(ReservationRequestDto reservationRequestDto, Long userId) {
        UserResponseDto user = userClient.getUserById(userId).getData();
        Reservation reservation = reservationRepository.findByIdOrElseThrow(reservationRequestDto.getReservationId());

        if(!reservation.getPossible()) {
            throw new BadRequestException("예약 불가능한 시간대입니다.");
        }

        if(reservation.getTime().minusMinutes(30).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("지난 시간은 예약이 불가능합니다");
        }

        List<Reservation> reservationList = reservationRepository.findByTime(reservationRequestDto.getTime());
        if(reservation.getInstrument().getName().equals("합주")){
            reservationList.stream()
                    .filter(r -> !r.getInstrument().getName().equals("합주"))
                    .forEach(r -> r.updateReservation(r.getTime(), false, r.getInstrument(), r.getUserId()));
        }else{
            reservationList.stream()
                    .filter(r -> r.getInstrument().getName().equals("합주"))
                    .forEach(r -> r.updateReservation(r.getTime(), false, r.getInstrument(), r.getUserId()));
        }

        reservation.updateReservation(
                reservation.getTime(),
                false,
                reservation.getInstrument(),
                userId
        );

        return ReservationResponseDto.from(reservation);
    }

    //예약 취소
    //신청자, 관리자, 매니저만 취소 가능
    //지난 예약은 취소 불가
    @Transactional
    public ReservationResponseDto cancelReservation(ReservationRequestDto reservationRequestDto, Long userId) {
        UserResponseDto user = userClient.getUserById(userId).getData();
        Reservation reservation = reservationRepository.findByIdOrElseThrow(reservationRequestDto.getReservationId());

        if(!reservation.getUserId().equals(userId)
                && !(Objects.equals(user.getUserRole(), UserRole.ADMIN.toString()) || Objects.equals(user.getUserRole(), UserRole.MANAGER.toString()))) {
            throw new BadRequestException("권한이 없는 사용자입니다");
        }

        if(reservation.getTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("지난 예약은 취소할 수 없습니다");
        }

        //합주 예약 취소인 경우 장비 예약 허용
        List<Reservation> reservationList = reservationRepository.findByTime(reservationRequestDto.getTime());
        if(reservation.getInstrument().getName().equals("합주")){
            reservationList.stream()
                    .filter(r -> !r.getInstrument().getName().equals("합주"))
                    .forEach(r -> r.updateReservation(r.getTime(), true, r.getInstrument(), r.getUserId()));
        }else{
            reservationList.stream()
                    .filter(r -> r.getInstrument().getName().equals("합주"))
                    .forEach(r -> r.updateReservation(r.getTime(), true, r.getInstrument(), r.getUserId()));
        }

        reservation.updateReservation(
                reservation.getTime(),
                true,
                reservation.getInstrument(),
                null
        );

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
    public List<ReservationResponseDto> findReservationByDayAndInst(ReservationRequestDto reservationRequestDto) {
        LocalDateTime startOfDay = LocalDateTime.now().minusDays(8).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfNextDay = LocalDateTime.now().minusDays(7).withHour(0).withMinute(0).withSecond(0);
        Instrument instrument = instrumentRepository.findByIdOrElseThrow(reservationRequestDto.getInstrumentId());


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