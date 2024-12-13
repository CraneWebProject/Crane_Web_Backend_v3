package crane.reservationservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long instrumentId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isActive;

    public void updateInstrument(String name, Boolean isActive) {
        this.name = name;
        this.isActive = isActive;
    }

    @Builder
    public Instrument(Long instrumentId,
                      String name,
                      Boolean isActive) {
        this.instrumentId = instrumentId;
        this.name = name;
        this.isActive = isActive;
    }
}
