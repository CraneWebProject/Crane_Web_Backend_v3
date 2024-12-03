package sch.crane.domain.reservation.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sch.crane.domain.common.exception.BadRequestException;
import sch.crane.domain.reservation.dto.ReservationRequestDto;
import sch.crane.domain.reservation.dto.ReservationResponseDto;
import sch.crane.domain.reservation.entity.Reservation;
import sch.crane.domain.reservation.entity.enums.Instrument;
import sch.crane.domain.reservation.entity.enums.Status;
import sch.crane.domain.reservation.exception.ReservationNotFoundException;
import sch.crane.domain.reservation.repository.ReservationRepository;
import sch.crane.domain.user.entity.User;
import sch.crane.domain.user.entity.enums.UserRole;
import sch.crane.domain.user.exception.UserNotFoundException;
import sch.crane.domain.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    //Batch 를 통해 미리 1주간 예약을 생성해 둠.
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

            if(reservationList.size() == 0){
                createReservationAfterNDays(i);
                openEnsembleAfterNDays(i);
                openInstAfterNDays(i);
            }
        }
    }



    //예약 생성
    //n일 이후 예약 전체 생성
    @Transactional
    public void createReservationAfterNDays(int n){
        LocalDateTime reservationDate = LocalDateTime.now().plusDays(n).withHour(0).withMinute(0).withSecond(0);

        //24시간동안 30분 단위로 예약 생성
        for(int hour = 8; hour < 24; hour++){
            for(int minute = 0; minute < 60; minute += 30){
                LocalDateTime time = reservationDate.plusHours(hour).plusMinutes(minute);

                List <Instrument> instruments = Stream.of(Instrument.values()).toList();

                for(Instrument instrument : instruments){
                    Reservation reservation = Reservation.builder()
                            .user(null)
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

        for(Reservation r : reservationList){
            if(r.getInstrument().equals(Instrument.ENSEMBLE)){
                r.updateReservation(r.getTime(), true, r.getInstrument(), r.getUser());
            }
        }
    }

    //n일 뒤 장비 예약 open
    @Transactional
    public void openInstAfterNDays(int n){
        LocalDateTime startOfDay = LocalDateTime.now().plusDays(n).withHour(0).withMinute(0);
        LocalDateTime endOfDay = LocalDateTime.now().plusDays(n).withHour(23).withMinute(59);

        List<Reservation> reservationList = reservationRepository.findAllByDate(startOfDay, endOfDay);

        for(Reservation r : reservationList){
            if(!r.getInstrument().equals(Instrument.ENSEMBLE)){
                r.updateReservation(r.getTime(), true, r.getInstrument(), r.getUser());
            }
        }
    }



    //단일 예약 생성
    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto reservationRequestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다"));

        Reservation reservation = Reservation.builder()
                .time(reservationRequestDto.getTime())
                .instrument(reservationRequestDto.getInstrument())
                .possible(false)
                .status(Status.PENDING)
                .user(user)
                .build();

        reservationRepository.save(reservation);
        return ReservationResponseDto.from(reservation);
    }

    //예약 확인
    public ReservationResponseDto getReservation(Long reservation_id){
        Reservation reservation = reservationRepository.findById(reservation_id)
                .orElseThrow(() -> new ReservationNotFoundException("예약을 찾을 수 없습니다."));
        return ReservationResponseDto.from(reservation);
    }


    //예약 수정
    //예약 시간, 예약 장비 수정 가능
    @Transactional
    public ReservationResponseDto updateReservation(ReservationRequestDto reservationRequestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        Reservation reservation = reservationRepository.findById(reservationRequestDto.getReservation_id())
                .orElseThrow(() -> new ReservationNotFoundException("예약을 찾을 수 없습니다."));

        //신청자 혹은 관리자, 매니저만 수정 가능
        if(!user.getUser_id().equals(reservation.getUser().getUser_id()) &&
                !(user.getUserRole() == UserRole.ADMIN || user.getUserRole() == UserRole.MANAGER)){
            throw new BadCredentialsException("권한이 없는 사용자입니다.");
        }

        reservation.updateReservation(
                reservationRequestDto.getTime(),
                reservation.getPossible(),
                reservationRequestDto.getInstrument(),
                reservation.getUser()
        );

        return ReservationResponseDto.from(reservation);
    }


    //예약하기
    @Transactional
    public ReservationResponseDto makeReservation(ReservationRequestDto reservationRequestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        Reservation reservation = reservationRepository.findById(reservationRequestDto.getReservation_id())
                .orElseThrow(() -> new ReservationNotFoundException("예약을 찾을 수 없습니다."));

        if(!reservation.getPossible()){
            throw new BadRequestException("이미 예약된 시간입니다.");
        }

        //합주 예약인 경우 장비 예약 불가 처리
        List<Reservation> reservationList = reservationRepository.findByTime(reservationRequestDto.getTime());
        if(reservation.getInstrument().equals(Instrument.ENSEMBLE)){
            for(Reservation r : reservationList){
                r.updateReservation(
                        r.getTime(),
                        false,
                        r.getInstrument(),
                        r.getUser()
                );
            }
        }else{
            //장비 예약인 경우 합주 예약 불가 처리
            for(Reservation r : reservationList){
                if(r.getInstrument().equals(Instrument.ENSEMBLE)){
                    r.updateReservation(
                            r.getTime(),
                            false,
                            r.getInstrument(),
                            r.getUser()
                    );
                }
            }
        }

        //예약 시간이 지난 경우, 예약 시간 30분 이내 예약 불가
        if(reservation.getTime().minusMinutes(30).isBefore(LocalDateTime.now())){
            throw new BadRequestException("지난 시간은 예약이 불가능합니다.");
        }

        reservation.updateReservation(
                reservation.getTime(),
                false,
                reservation.getInstrument(),
                user
        );

        return ReservationResponseDto.from(reservation);
    }


    //예약 취소
    //신청자 혹은 관리자, 매니저만 취소 가능
    //지난 예약에 대한 취소 불가
    @Transactional
    public ReservationResponseDto cancelReservation(ReservationRequestDto reservationRequestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        Reservation reservation = reservationRepository.findById(reservationRequestDto.getReservation_id())
                .orElseThrow(() -> new ReservationNotFoundException("예약을 찾을 수 없습니다."));

        if(!user.getUser_id().equals(reservation.getUser().getUser_id()) &&
                !(user.getUserRole() == UserRole.ADMIN || user.getUserRole() == UserRole.MANAGER)){
            throw new BadCredentialsException("권한이 없는 사용자입니다.");
        }

        if(reservation.getTime().isBefore(LocalDateTime.now())){
            throw new BadRequestException("지난 예약은 취소할 수 없습니다.");
        }

        //합주 예약 취소인 경우 장비 예약 허용으로 변경
        List<Reservation> reservationList = reservationRepository.findByTime(reservationRequestDto.getTime());

        if(reservation.getInstrument().equals(Instrument.ENSEMBLE)){
            for(Reservation r : reservationList){
                r.updateReservation(
                        r.getTime(),
                        true,
                        r.getInstrument(),
                        null
                );
            }
        }else {
            //장비 예약 취소인 경우, 합주 예약 가능하도록 변경
            for(Reservation r : reservationList){
                if(r.getInstrument().equals(Instrument.ENSEMBLE)){
                    r.updateReservation(
                            r.getTime(),
                            true,
                            r.getInstrument(),
                            null
                    );
                }
            }
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
    //batch로 구현
    //새벽 n시에 전 주 예약되지 않은 시간만 삭제
    @Transactional
    public void deleteReservation(){
        LocalDateTime startOfDay = LocalDateTime.now().minusDays(8).withHour(0).withMinute(0);
        LocalDateTime endOfDay = LocalDateTime.now().minusDays(8).withHour(23).withMinute(59);

        List<Reservation> reservationList = reservationRepository.findAllByDate(startOfDay, endOfDay);

        for(Reservation r : reservationList){
            if(r.getUser() == null){
                reservationRepository.delete(r);
            }
        }
    }


    //일간 장비별 예약 목록
    public List<ReservationResponseDto> findReservationByDayAndInst(ReservationRequestDto reservationRequestDto) {
        LocalDateTime startOfDay = reservationRequestDto.getTime().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = reservationRequestDto.getTime().toLocalDate().atTime(LocalTime.MAX);

        List<Reservation> reservationList = reservationRepository.findAllByResDateAndInstrument(
                startOfDay,
                endOfDay,
                reservationRequestDto.getInstrument()
        );

        List<ReservationResponseDto> reservationResponseDtoList = new ArrayList<>();

        for (Reservation r : reservationList) {
            ReservationResponseDto reservationResponseDto = ReservationResponseDto.from(r);
            reservationResponseDtoList.add(reservationResponseDto);
        }

        return reservationResponseDtoList;
    }

    //사용자 별 예약목록
    public List<ReservationResponseDto> findReservationByUser(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        List<Reservation> reservationList = reservationRepository.findByUserEmail(email);

        List<ReservationResponseDto> reservationResponseDtoList = new ArrayList<>();
        for (Reservation r : reservationList) {
            ReservationResponseDto reservationResponseDto = ReservationResponseDto.from(r);
            reservationResponseDtoList.add(reservationResponseDto);
        }

        return reservationResponseDtoList;
    }

}
