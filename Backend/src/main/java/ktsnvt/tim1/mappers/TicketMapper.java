package ktsnvt.tim1.mappers;

import ktsnvt.tim1.DTOs.TicketDTO;
import ktsnvt.tim1.model.EventDay;
import ktsnvt.tim1.model.ReservableSeatGroup;
import ktsnvt.tim1.model.Seat;
import ktsnvt.tim1.model.Ticket;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class TicketMapper implements IMapper<Ticket, TicketDTO> {
    @Override
    public Ticket toEntity(TicketDTO dto) throws Exception {
        return null;
    }

    @Override
    public TicketDTO toDTO(Ticket ticket) {
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setId(ticket.getId());
        if (!ticket.getSeats().isEmpty()) {
            Seat seat = ticket.getSeats().iterator().next();
            ticketDTO.setRowNum(seat.getRowNum());
            ticketDTO.setColNum(seat.getColNum());
        }
        ReservableSeatGroup reservableSeatGroup = ticket.getReservableSeatGroups().iterator().next();
        ticketDTO.setSeatGroupName(reservableSeatGroup.getEventSeatGroup().getSeatGroup().getName());
        ticketDTO.setPrice(reservableSeatGroup.getEventSeatGroup().getPrice());
        ticketDTO.setEventDays(ticket.getReservableSeatGroups().stream().map(ReservableSeatGroup::getEventDay)
                .map(EventDay::getDate).collect(Collectors.toCollection(ArrayList::new)));
        return ticketDTO;
    }
}
