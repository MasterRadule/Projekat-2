package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.NewReservationDTO;
import ktsnvt.tim1.DTOs.ReservationDTO;
import ktsnvt.tim1.DTOs.ReservationTypeDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.Event;
import ktsnvt.tim1.model.EventDay;
import ktsnvt.tim1.repositories.EventRepository;
import ktsnvt.tim1.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EventRepository eventRepository;

    public Page<ReservationDTO> getReservations(ReservationTypeDTO type, Pageable pageable) {
        switch (type) {
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

    public ReservationDTO createReservation(NewReservationDTO newReservationDTO) throws EntityNotFoundException, EntityNotValidException {
        Event event = eventRepository.findByIsActiveForReservationsTrueAndIsCancelledFalseAndById(newReservationDTO.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        Date firstEventDay = event.getEventDays().stream().map(EventDay::getDate).min(Date::compareTo).get();
        int numOfDaysToEvent = (int) TimeUnit.DAYS.convert(Math.abs(firstEventDay.getTime() - new Date().getTime()), TimeUnit.MILLISECONDS);
        if(numOfDaysToEvent<0)  throw new EntityNotValidException("Event already started");
        if (numOfDaysToEvent <= event.getReservationDeadlineDays())
            throw new EntityNotValidException("Reservation deadline date passed");

        event.getReservationDeadlineDays();
        return null;
    }

}
