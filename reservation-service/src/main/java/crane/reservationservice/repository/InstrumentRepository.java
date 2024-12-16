package crane.reservationservice.repository;

import crane.reservationservice.entity.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstrumentRepository extends JpaRepository<Instrument, Long> {


    Optional<Instrument> findByName(String name);

    default Instrument findByIdOrElseThrow(long id) {
        return findById(id).orElseThrow(() -> new RuntimeException(id + "는 존재하지 않는 장비입니다."));
    }

}
