package ktsnvt.tim1.DTOs;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

public class EventSeatGroupDTO {

    @NotNull(message = "Seat group ID cannot be null")
    private Long seatGroupID;

    @Positive
    private Double price;
    private Set<ReservableSeatGroupDTO> reservableSeatGroups;

    public EventSeatGroupDTO() {}

    public EventSeatGroupDTO(Long seatGroupID) {
        this.seatGroupID = seatGroupID;
    }

    public Long getSeatGroupID() {
        return seatGroupID;
    }

    public void setSeatGroupID(Long seatGroupID) {
        this.seatGroupID = seatGroupID;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Set<ReservableSeatGroupDTO> getReservableSeatGroups() {
        return reservableSeatGroups;
    }

    public void setReservableSeatGroups(Set<ReservableSeatGroupDTO> reservableSeatGroups) {
        this.reservableSeatGroups = reservableSeatGroups;
    }
}
