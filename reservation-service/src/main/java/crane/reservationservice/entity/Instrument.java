package crane.reservationservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long instrument_id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isActive;
}
