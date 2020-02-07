package ktsnvt.tim1.DTOs;

import java.util.Set;

public class ReservableSeatGroupDTO {
    private Long id;
    private Long esgID;
    private Long eventDayID;
    private Set<SeatDTO> seats;
    private int freeSeats;

    public ReservableSeatGroupDTO() {
    }

    public ReservableSeatGroupDTO(Long id, Long esgID, Long eventDayID, Set<SeatDTO> seats, int freeSeats) {
        this.id = id;
        this.esgID = esgID;
        this.eventDayID = eventDayID;
        this.seats = seats;
        this.freeSeats = freeSeats;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventDayID() {
        return eventDayID;
    }

    public void setEventDayID(Long eventDayID) {
        this.eventDayID = eventDayID;
    }

    public Set<SeatDTO> getSeats() {
        return seats;
    }

    public void setSeats(Set<SeatDTO> seats) {
        this.seats = seats;
    }

    public Long getEsgID() {
        return esgID;
    }

    public void setEsgID(Long esgID) {
        this.esgID = esgID;
    }

    public int getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(int freeSeats) {
        this.freeSeats = freeSeats;
    }
}
