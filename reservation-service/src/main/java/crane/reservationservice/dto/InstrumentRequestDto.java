package crane.reservationservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InstrumentRequestDto {

    private Long instrumentId;
    private String name;
    private Boolean isActive;

    @Builder
    public InstrumentRequestDto(Long instrumentId,
                                String name,
                                boolean isActive) {
        this.instrumentId = instrumentId;
        this.name = name;
        this.isActive = isActive;
    }
}
