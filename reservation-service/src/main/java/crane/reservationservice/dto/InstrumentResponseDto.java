package crane.reservationservice.dto;

import crane.reservationservice.entity.Instrument;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InstrumentResponseDto {
    private Long instrumentId;
    private String name;
    private Boolean isActive;

    @Builder
    public InstrumentResponseDto(Long instrumentId,
                                 String name,
                                 Boolean isActive) {
        this.instrumentId = instrumentId;
        this.name = name;
        this.isActive = isActive;
    }

    public static InstrumentResponseDto from(Instrument instrument) {
        return InstrumentResponseDto.builder()
                .instrumentId(instrument.getInstrumentId())
                .name(instrument.getName())
                .isActive(instrument.getIsActive())
                .build();
    }
}
