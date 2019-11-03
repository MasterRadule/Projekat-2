package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.ReservationDTO;
import ktsnvt.tim1.DTOs.ReservationTypeDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    public Page<ReservationDTO> getReservations(ReservationTypeDTO type, Pageable pageable) {
        switch(type){
            case BOUGHT:
                return reservationRepository.findByOrderIdIsNotNull(pageable).map(ReservationDTO::new);
            case RESERVED:
                return reservationRepository.findByOrderIdIsNull(pageable).map(ReservationDTO::new);
            default:
                return reservationRepository.findAll(pageable).map(ReservationDTO::new);

        }
    }

    public ReservationDTO getReservation(Long id) throws EntityNotFoundException {
        return new ReservationDTO(reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found")));
    }
}
