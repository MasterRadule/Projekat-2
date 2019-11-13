package ktsnvt.tim1.mappers;

import ktsnvt.tim1.DTOs.ReservationDTO;
import ktsnvt.tim1.DTOs.TicketDTO;
import ktsnvt.tim1.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class ReservationMapper implements  IMapper<Reservation, ReservationDTO> {

    @Autowired
    private TicketMapper ticketMapper;

    @Override
    public Reservation toEntity(ReservationDTO dto) throws Exception {
        return null;
    }

    @Override
    public ReservationDTO toDTO(Reservation reservation) {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setId(reservation.getId());
        reservationDTO.setOrderId(reservation.getOrderId());
        reservationDTO.setTickets(reservation.getTickets().stream().map(ticketMapper::toDTO).collect(Collectors.toCollection(ArrayList::new)));
        reservationDTO.setEventId(reservation.getEvent().getId());
        reservationDTO.setEventName(reservation.getEvent().getName());
        return reservationDTO;
    }
}
