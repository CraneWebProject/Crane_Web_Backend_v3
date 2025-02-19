package crane.reservationservice.controller;

import crane.reservationservice.common.advice.ApiResponse;
import crane.reservationservice.dto.ReservationRequestDto;
import crane.reservationservice.dto.ReservationResponseDto;
import crane.reservationservice.entity.Reservation;
import crane.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    //예약 생성
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponseDto>> createReservation(@RequestHeader("X-Authenticated-User") Long userId,
                                                                                 @RequestBody ReservationRequestDto reservationRequestDto) {
        ReservationResponseDto reservationResponseDto = reservationService.createReservation(reservationRequestDto, userId);
        return ResponseEntity.ok(ApiResponse.success("예약 생성 성공", reservationResponseDto));
    }

    //예약 확인
    @GetMapping("/{resId}")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> getReservation(@PathVariable("resId") Long resId) {
        ReservationResponseDto reservationResponseDto = reservationService.getReservation(resId);
        return ResponseEntity.ok(ApiResponse.success("예약 조회 성공", reservationResponseDto));
    }

    //예약 수정
    @PutMapping
    public ResponseEntity<ApiResponse<ReservationResponseDto>> updateReservation(@RequestHeader("X-Authenticated-User") Long userId,
                                                                                 @RequestBody ReservationRequestDto reservationRequestDto) {
        ReservationResponseDto reservationResponseDto = reservationService.updateReservation(reservationRequestDto, userId);
        return ResponseEntity.ok(ApiResponse.success("예약 수정 성공", reservationResponseDto));
    }

    //예약하기
    @GetMapping("/make")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> makeAReservation(@RequestHeader("X-Authenticated-User") Long userId,
                                                                                @RequestParam (value = "reservationId") Long reservationId ) {
        ReservationResponseDto reservationResponseDto = reservationService.makeReservation(reservationId, userId);
        return ResponseEntity.ok(ApiResponse.success("예약 성공", reservationResponseDto));
    }

    //예약 취소
    @GetMapping("/cancel")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> cancelReservation(@RequestHeader("X-Authenticated-User") Long userId,
                                                                                 @RequestParam (value = "reservationId") Long reservationId ) {
        ReservationResponseDto reservationResponseDto = reservationService.cancelReservation(reservationId, userId);
        return ResponseEntity.ok(ApiResponse.success("예약 취소 성공", reservationResponseDto));
    }

    //일간 장비별 예약목록
    @GetMapping("/listbydate")
    public ResponseEntity<ApiResponse<List<ReservationResponseDto>>> getListByDateAndInst(@RequestParam(value = "date")
                                                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                                          LocalDate date,
                                                                                          @RequestParam(value = "instrumentId") long instrumentId
    ) {
        List<ReservationResponseDto> reservationResponseDtoList = reservationService.findReservationByDayAndInst(date, instrumentId);
        return ResponseEntity.ok(ApiResponse.success("일간 장비별 예약목록 조회 성공", reservationResponseDtoList));
    }

    //사용자별 예약 목록
    @GetMapping("/listbyuser")
    public ResponseEntity<ApiResponse<List<ReservationResponseDto>>> getListByUser(@RequestHeader("X-Authenticated-User") Long userId) {
        List<ReservationResponseDto> reservationResponseDtoList = reservationService.findReservationByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("사용자 별 예약 목록 조회 성공", reservationResponseDtoList));
    }

    @PostMapping("/init")
    public ResponseEntity<ApiResponse<Void>> initReservation(){
        reservationService.initReservation();
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("init-nextweek")
    public ResponseEntity<ApiResponse<Void>> initNextWeekReservation(){
        reservationService.createReservationAfterNDays(8);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("open-ensemble")
    public ResponseEntity<ApiResponse<Void>> openEnSambleReservation(){
        reservationService.openEnsembleAfterNDays(7);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("open-inst")
    public ResponseEntity<ApiResponse<Void>> openInstReservation(){
        reservationService.openInstAfterNDays(7);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
