package ktsnvt.tim1.DTOs;

import ktsnvt.tim1.model.Reservation;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ReservationDTO {

    private Long id;

    private String orderId;

    private String eventName;

    private Long eventId;

    private ArrayList<TicketDTO> tickets;

    public ReservationDTO() {
    }

    public ReservationDTO(Long id, String orderId, String eventName, Long eventId, ArrayList<TicketDTO> tickets) {
        this.id = id;
        this.orderId = orderId;
        this.eventName = eventName;
        this.eventId = eventId;
        this.tickets = tickets;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public ArrayList<TicketDTO> getTickets() {
        return tickets;
    }

    public void setTickets(ArrayList<TicketDTO> tickets) {
        this.tickets = tickets;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
