package crane.reservationservice.service;

import crane.reservationservice.dto.InstrumentRequestDto;
import crane.reservationservice.dto.InstrumentResponseDto;
import crane.reservationservice.entity.Instrument;
import crane.reservationservice.entity.Reservation;
import crane.reservationservice.repository.InstrumentRepository;
import crane.reservationservice.repository.ReservationRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public InstrumentResponseDto createInstrument(InstrumentRequestDto requestDto) {
        //중복 name 체크
        Optional<Instrument> optionalInstrument = instrumentRepository.findByName(requestDto.getName());
        if(optionalInstrument.isPresent()){
            throw new EntityExistsException("이미 존재하는 장비명입니다.");
        }

        Instrument instrument = Instrument.builder()
                .name(requestDto.getName())
                .isActive(true) //생성시 항상 활성화
                .build();

        instrumentRepository.save(instrument);
        return InstrumentResponseDto.from(instrument);
    }

    public List<InstrumentResponseDto> getAllInstruments() {
        List<Instrument> instrumentList = instrumentRepository.findAll();
        return instrumentList.stream()
                .map(InstrumentResponseDto::from)
                .toList();
    }


    @Transactional
    public InstrumentResponseDto updateInstrument(InstrumentRequestDto requestDto) {
        Instrument instrument = instrumentRepository.findByIdOrElseThrow(requestDto.getInstrumentId());

        //null 값일 경우 기존 값으로 유지
        String updateName = requestDto.getName() != null
                ? requestDto.getName()
                : instrument.getName();

        Boolean updateIsActive = requestDto.getIsActive() != null
                ?requestDto.getIsActive()
                : instrument.getIsActive();

        instrument.updateInstrument(updateName, updateIsActive);
        return InstrumentResponseDto.from(instrument);
    }

    //삭제시 관련 예약 instrument null 로 바꿈
    @Transactional
    public void deleteInstrument(Long instrumentId) {
        List<Reservation> reservationList = reservationRepository.findAllByInstrument(instrumentId);

        reservationList.stream()
                .forEach(r -> r.updateReservation(r.getTime(), r.getPossible(), null, r.getUserId(), null));

        reservationRepository.deleteById(instrumentId);


    }


}
