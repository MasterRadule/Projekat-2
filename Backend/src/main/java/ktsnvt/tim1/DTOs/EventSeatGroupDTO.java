package ktsnvt.tim1.DTOs;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public class EventSeatGroupDTO {

    @NotNull(message = "Seat group ID cannot be null")
    private Long seatGroupID;

    @Positive
    private Double price;

    @PositiveOrZero
    private Integer freeSeats;

    public EventSeatGroupDTO() {}

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

    public Integer getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(Integer freeSeats) {
        this.freeSeats = freeSeats;
    }
}
