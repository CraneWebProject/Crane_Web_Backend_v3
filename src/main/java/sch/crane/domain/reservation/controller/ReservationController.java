package sch.crane.domain.reservation.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sch.crane.domain.common.advice.ApiResponse;
import sch.crane.domain.common.dto.CustomUserDetails;
import sch.crane.domain.reservation.dto.ReservationRequestDto;
import sch.crane.domain.reservation.dto.ReservationResponseDto;
import sch.crane.domain.reservation.service.ReservationService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    //예약 생성
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponseDto>> creatReservation(@AuthenticationPrincipal CustomUserDetails authUser,
                                                                                @RequestBody ReservationRequestDto reservationRequestDto) {
        ReservationResponseDto reservationResponseDto = reservationService.createReservation(reservationRequestDto, authUser.getEmail());
        return ResponseEntity.ok(ApiResponse.success("예약생성 성공", reservationResponseDto));
    }

    //예약 확인
    @GetMapping("/{resId}")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> getReservation(@PathVariable Long resId) {
        return ResponseEntity.ok(ApiResponse.success("예약 조회 성공", reservationService.getReservation(resId)));
    }

    //예약 수정
    @PutMapping("/{resId}")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> updateReservation(@PathVariable Long resId,
                                                                                 @AuthenticationPrincipal CustomUserDetails authUser,
                                                                                 @RequestBody ReservationRequestDto reservationRequestDto) {
        ReservationResponseDto reservationResponseDto = reservationService.updateReservation(reservationRequestDto, authUser.getEmail());
        return ResponseEntity.ok(ApiResponse.success("예약 수정 성공", reservationResponseDto));
    }

    //예약 하기
    @GetMapping("/make")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> makeAReservation(@AuthenticationPrincipal CustomUserDetails authUser,
                                                                                @RequestBody ReservationRequestDto reservationRequestDto) {
        ReservationResponseDto reservationResponseDto = reservationService.makeReservation(reservationRequestDto, authUser.getEmail());
        return ResponseEntity.ok(ApiResponse.success("예약 성공", reservationResponseDto));
    }

    //예약 취소
    @GetMapping("cancel")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> cancelReservation(@AuthenticationPrincipal CustomUserDetails authUser,
                                                                                 @RequestBody ReservationRequestDto reservationRequestDto) {
        ReservationResponseDto reservationResponseDto = reservationService.cancelReservation(reservationRequestDto, authUser.getEmail());
        return ResponseEntity.ok(ApiResponse.success("예약 취소 성공", reservationResponseDto));
    }

    //일간 장비별 예약 목록
    @GetMapping("/date")
    public ResponseEntity<ApiResponse<List<ReservationResponseDto>>> getReservationByDate(@RequestBody ReservationRequestDto reservationRequestDto) {
        List<ReservationResponseDto> reservationResponseDtoList = reservationService.findReservationByDayAndInst(reservationRequestDto);
        return ResponseEntity.ok(ApiResponse.success("일간 장비별 예약 목록 조회 성공", reservationResponseDtoList));
    }

    //사용자별 예약목록
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<ReservationResponseDto>>> getReservationByUser(@AuthenticationPrincipal CustomUserDetails authUser) {
        List<ReservationResponseDto> reservationResponseDtoList = reservationService.findReservationByUser(authUser.getEmail());
        return ResponseEntity.ok(ApiResponse.success("사용자별 예약 목록 조회 성공", reservationResponseDtoList));
    }

}
