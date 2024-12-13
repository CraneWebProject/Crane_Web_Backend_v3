package crane.reservationservice.controller;

import crane.reservationservice.common.advice.ApiResponse;
import crane.reservationservice.dto.ReservationRequestDto;
import crane.reservationservice.dto.ReservationResponseDto;
import crane.reservationservice.entity.Reservation;
import crane.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                                                                @RequestBody ReservationRequestDto reservationRequestDto) {
        ReservationResponseDto reservationResponseDto = reservationService.makeReservation(reservationRequestDto, userId);
        return ResponseEntity.ok(ApiResponse.success("예약 성공", reservationResponseDto));
    }

    //예약 취소
    @GetMapping("/cancel")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> cancelReservation(@RequestHeader("X-Authenticated-User") Long userId,
                                                                                 @RequestBody ReservationRequestDto reservationRequestDto) {
        ReservationResponseDto reservationResponseDto = reservationService.cancelReservation(reservationRequestDto, userId);
        return ResponseEntity.ok(ApiResponse.success("예약 취소 성공", reservationResponseDto));
    }

    //일간 장비별 예약목록
    @GetMapping("/listbydate")
    public ResponseEntity<ApiResponse<List<ReservationResponseDto>>> getListByDateAndInst(@RequestBody ReservationRequestDto reservationRequestDto) {
        List<ReservationResponseDto> reservationResponseDtoList = reservationService.findReservationByDayAndInst(reservationRequestDto);
        return ResponseEntity.ok(ApiResponse.success("일간 장비별 예약목록 조회 성공", reservationResponseDtoList));
    }

    //사용자별 예약 목록
    @GetMapping("/listbyuser")
    public ResponseEntity<ApiResponse<List<ReservationResponseDto>>> getListByUser(@RequestHeader("X-Authenticated-User") Long userId) {
        List<ReservationResponseDto> reservationResponseDtoList = reservationService.findReservationByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("사용자 별 예약 목록 조회 성공", reservationResponseDtoList));
    }

}
