package ktsnvt.tim1.DTOs;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

public class NewReservationDTO {

    @NotNull
    private Long eventId;
    @NotEmpty
    private ArrayList<NewTicketDTO> tickets;

    public NewReservationDTO() {
    }

    public NewReservationDTO(@NotNull Long eventId, @NotEmpty ArrayList<NewTicketDTO> tickets) {
        this.eventId = eventId;
        this.tickets = tickets;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public ArrayList<NewTicketDTO> getTickets() {
        return tickets;
    }

    public void setTickets(ArrayList<NewTicketDTO> tickets) {
        this.tickets = tickets;
    }
}
