package crane.reservationservice.controller;

import crane.reservationservice.common.advice.ApiResponse;
import crane.reservationservice.dto.InstrumentRequestDto;
import crane.reservationservice.dto.InstrumentResponseDto;
import crane.reservationservice.service.InstrumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/instruments")
public class InstrumentController {

    private final InstrumentService instrumentService;

    @PostMapping
    public ResponseEntity<ApiResponse<InstrumentResponseDto>> createInstrument(InstrumentRequestDto instrumentDto) {
        InstrumentResponseDto instrument = instrumentService.createInstrument(instrumentDto);
        return ResponseEntity.ok(ApiResponse.success("장비 추가 완료", instrument));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InstrumentResponseDto>>> getAllInstruments() {
        List<InstrumentResponseDto> instruments = instrumentService.getAllInstruments();
        return ResponseEntity.ok(ApiResponse.success("장비 목록 조회 성공", instruments));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<InstrumentResponseDto>> updateInstrument(InstrumentRequestDto instrumentDto) {
        InstrumentResponseDto instrument = instrumentService.updateInstrument(instrumentDto);
        return ResponseEntity.ok(ApiResponse.success("장비 수정 성공", instrument));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<?>> deleteInstrument(InstrumentRequestDto instrumentDto) {
        instrumentService.deleteInstrument(instrumentDto.getInstrumentId());
        return ResponseEntity.ok(ApiResponse.success("장비 삭제 성공"));
    }

}
